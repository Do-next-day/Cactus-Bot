package org.laolittle.plugin.genshin

import io.ktor.client.request.*
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import net.mamoe.mirai.console.plugin.description.PluginDependency
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.buildForwardMessage
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.message.nextMessageOrNull
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.info
import org.jetbrains.skia.EncodedImageFormat
import org.laolittle.plugin.genshin.api.ApiAccessDeniedException
import org.laolittle.plugin.genshin.api.bbs.BBSApi
import org.laolittle.plugin.genshin.api.bbs.BBSData
import org.laolittle.plugin.genshin.api.genshin.GenshinBBSApi
import org.laolittle.plugin.genshin.api.internal.client
import org.laolittle.plugin.genshin.api.internal.getAppVersion
import org.laolittle.plugin.genshin.database.cactusSuspendedTransaction
import org.laolittle.plugin.genshin.database.getUserData
import org.laolittle.plugin.genshin.model.GachaSimulator.gachaCharacter
import org.laolittle.plugin.genshin.model.GachaSimulator.renderGachaImage
import org.laolittle.plugin.genshin.util.characterDataFolder
import org.laolittle.plugin.genshin.util.gachaDataFolder
import org.laolittle.plugin.genshin.util.requireCookie
import org.laolittle.plugin.genshin.util.signGenshin
import org.laolittle.plugin.toExternalResource

object CactusBot : KotlinPlugin(JvmPluginDescription(
    id = "org.laolittle.plugin.GenshinHelper",
    name = "Genshin-Helper",
    version = "1.0",
) {
    author("LaoLittle")
    dependsOn(
        PluginDependency("org.laolittle.plugin.SkikoMirai", ">=1.0.2")
    )
}) {
    private val users = mutableSetOf<Long>()
    override fun onEnable() {
        init()
        logger.info { "Plugin loaded" }

        globalEventChannel().subscribeGroupMessages {
            (startsWith("原神") or startsWith("派蒙")) Listener@{ foo ->
                val result = Regex("""(人物|十连|单抽|查询|test)(.*)""").find(foo)?.groupValues
                when (result?.get(1)) {
                    "十连" -> {
                        if (!users.add(sender.id)) return@Listener
                        val entities = gachaCharacter(sender.id, 1, 10)
                        renderGachaImage(entities).toExternalResource(EncodedImageFormat.JPEG).use { ex ->
                            subject.sendMessage(ex.uploadAsImage(subject) + message.quote())
                        }
                        users.remove(sender.id)
                    }
                    "查询" -> {
                        val userData = if (CactusConfig.allowAnonymous) getUserData(sender.id)
                        else requireCookie { return@Listener }

                        val cookies = userData.data.cookies.takeIf { it.isNotBlank() } ?: CactusData.cookies

                        val uid = result[2].replace(Regex("""[\s]+"""), "").toLongOrNull()
                        if (uid == null || uid < 100000100 || uid > 700000000) {
                            subject.sendMessage("请输入正确的uid")
                            return@Listener
                        }
                        val query = kotlin.runCatching {
                            GenshinBBSApi.getPlayerInfo(uid, cookies, userData.data.uuid)
                        }.getOrElse {
                            when (it) {
                                is SerializationException -> subject.sendMessage("请求失败！请检查uid是否正确")
                                is ApiAccessDeniedException -> subject.sendMessage("获取失败: ${it.message}")
                                else -> logger.error(it)
                            }
                            return@Listener
                        }

                        subject.sendMessage(buildForwardMessage {
                            query.avatars.forEach { cInfo ->
                                add(bot, buildMessageChain {
                                    add(client.get<ByteArray>(cInfo.imageUrl).toExternalResource()
                                        .use { subject.uploadImage(it) })
                                    add(
                                        """
                                        名称: ${cInfo.name}
                                        命座: ${cInfo.constellation}
                                    """.trimIndent()
                                    )
                                })
                            }
                        })
                    }

                    "test" -> {


                    }
                }
            }
        }

        globalEventChannel().subscribeFriendMessages {
            "原神登录" Login@{
                subject.sendMessage("请发送Cookie")
                val cookies = nextMessageOrNull(30_000)?.content ?: kotlin.run {
                    subject.sendMessage("超时")
                    return@Login
                }

                val response = runCatching {
                    val roles = BBSApi.getRolesByCookie(cookies, BBSData.GameBiz.HK4E_CN)

                    if (roles.isEmpty()) {
                        subject.sendMessage("没有找到绑定的角色!")
                        return@Login
                    }

                    var role = roles.first()
                    cactusSuspendedTransaction {
                        val userData = getUserData(subject.id)
                        val data = userData.data
                        // roles not empty
                        if (roles.size == 1) {
                            data.cookies = cookies
                        } else {
                            subject.sendMessage(buildMessageChain {
                                add("查询到以下信息: \n")
                                roles.forEach { r ->
                                    add("昵称: ${r.nickname}\nuid: ${r.gameUID}\n")
                                }
                                add("请发送uid进行绑定")
                            })
                            nextMessageOrNull(30_000) { e ->
                                val uid = e.message.content.toLongOrNull() ?: return@nextMessageOrNull false
                                role = roles.firstOrNull { r -> r.gameUID == uid } ?: kotlin.run {
                                    return@nextMessageOrNull false
                                }
                                true
                            } ?: kotlin.run {
                                subject.sendMessage("超时")
                                return@cactusSuspendedTransaction null
                            }

                            data.cookies = cookies
                        }

                        userData.genshinUID = role.gameUID
                        userData.data = data
                    } ?: return@Login
                    role
                }.getOrElse {
                    subject.sendMessage("登录失败, 原因: ${it.message}")
                    return@Login
                }

                subject.sendMessage(
                    """
                    旅行者${response.nickname}登录成功!
                    你的UID: ${response.gameUID}
                """.trimIndent()
                )
            }
        }

        globalEventChannel().subscribeMessages {
            "原神签到" Sign@{
                val userData = requireCookie { return@Sign }

                kotlin.runCatching {
                    userData.signGenshin()
                }.onSuccess {
                    subject.sendMessage("旅行者: ${userData.genshinUID}签到成功")
                }.onFailure {
                    subject.sendMessage("签到失败: ${it.message}")
                }

            }
        }
    }

    private fun init() {
        launch { getAppVersion(true) }
        // GenshinTimerProvider.start()
        CactusConfig.reload()
        CactusData.reload()
        dataFolder.mkdirs()
        gachaDataFolder.mkdir()
        characterDataFolder.mkdir()
    }

}