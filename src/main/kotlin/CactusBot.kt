package org.laolittle.plugin.genshin

import io.ktor.client.request.*
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
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
import org.laolittle.plugin.toExternalResource

object CactusBot : KotlinPlugin(JvmPluginDescription(
    id = "org.laolittle.plugin.GenshinHelper",
    name = "Genshin-Helper",
    version = "1.0",
) {
    author("LaoLittle")
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
                        val uid = result[2].replace(Regex("""[\s]+"""), "").toLongOrNull()
                        if (uid == null || uid < 100000100 || uid > 700000000) {
                            subject.sendMessage("请输入正确的uid")
                            return@Listener
                        }
                        val query = kotlin.runCatching {
                            GenshinBBSApi.getPlayerInfo(uid)
                        }.getOrElse {
                            when (it) {
                                is SerializationException -> subject.sendMessage("请求失败！请检查uid是否正确")
                                is IllegalAccessException -> subject.sendMessage("获取失败: ${it.message}")
                                else -> logger.info(it)
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
            startsWith("原神登录") Listener@{
                subject.sendMessage("请发送Cookie")
                val cookies = nextMessageOrNull(30_000)?.content ?: kotlin.run {
                    subject.sendMessage("超时")
                    return@Listener
                }

                val response = runCatching {
                    val roles = BBSApi.getRolesByCookie(cookies, BBSData.GameBiz.HK4E_CN)
                    if (roles.isEmpty()) {
                        subject.sendMessage("没有找到绑定的角色！")
                        return@Listener
                    }
                    var role = roles.first()
                    cactusSuspendedTransaction {
                        val userData = getUserData(subject.id)
                        val data = userData.data
                        if (roles.size == 1) {
                            data.setCookies(cookies)
                        } else {
                            var uid: Long
                            nextMessageOrNull(30_000) { e ->
                                uid = e.message.content.toLongOrNull() ?: return@nextMessageOrNull false
                                role = roles.firstOrNull { r -> r.gameUID == uid } ?: kotlin.run {
                                    subject.sendMessage("超时")
                                    return@nextMessageOrNull false
                                }
                                true
                            } ?: return@cactusSuspendedTransaction null

                            data.setCookies(cookies)
                        }

                        userData.genshinUID = role.gameUID
                        userData.data = data
                    } ?: return@Listener
                    role
                }.getOrElse {
                    subject.sendMessage("登录失败，原因: ${it.message}")
                    return@Listener
                }

                subject.sendMessage(
                    """
                    旅行者${response.nickname}登录成功！
                    你的UID: ${response.gameUID}
                """.trimIndent()
                )
            }
        }

        globalEventChannel().subscribeMessages {
            "原神签到" Sign@{
                val userData = cactusSuspendedTransaction {
                    getUserData(subject.id)
                }
                println(userData.data.toString())
                val cookies = userData.data.cookieData.cookies
                val uuid = userData.data.cookieData.uuid
                if (cookies == null) {
                    subject.sendMessage("请先登录")
                    return@Sign
                }

                val region = BBSApi.getRolesByCookie(cookies, BBSData.GameBiz.HK4E_CN)
                    .find { r -> r.gameUID == userData.genshinUID }?.region ?: kotlin.run {
                    subject.sendMessage("遇到预料外的错误")
                    return@Sign
                }

                subject.sendMessage(GenshinBBSApi.signGenshin(userData.genshinUID, region, cookies, uuid))
            }
        }
    }

    private fun init() {
        launch { getAppVersion(true) }
        CactusConfig.reload()
        CactusData.reload()
        dataFolder.mkdirs()
        gachaDataFolder.mkdir()
        characterDataFolder.mkdir()
    }

}