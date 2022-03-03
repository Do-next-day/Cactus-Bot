package org.laolittle.plugin.genshin

import io.ktor.client.request.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.atTime
import kotlinx.datetime.toKotlinLocalDateTime
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
import org.laolittle.plugin.genshin.CactusConfig.guideMessage
import org.laolittle.plugin.genshin.api.ApiAccessDeniedException
import org.laolittle.plugin.genshin.api.bbs.BBSApi
import org.laolittle.plugin.genshin.api.bbs.data.GameRole
import org.laolittle.plugin.genshin.api.genshin.GenshinBBSApi
import org.laolittle.plugin.genshin.api.internal.client
import org.laolittle.plugin.genshin.api.internal.getAppVersion
import org.laolittle.plugin.genshin.database.cactusSuspendedTransaction
import org.laolittle.plugin.genshin.database.getUserData
import org.laolittle.plugin.genshin.mirai.messageContext
import org.laolittle.plugin.genshin.model.GachaSimulator.gachaCharacter
import org.laolittle.plugin.genshin.model.GachaSimulator.renderGachaImage
import org.laolittle.plugin.genshin.service.GenshinGachaCache
import org.laolittle.plugin.genshin.service.GenshinSignProver
import org.laolittle.plugin.genshin.util.*
import org.laolittle.plugin.toExternalResource
import java.time.LocalDate as JLocalDate

object CactusBot : KotlinPlugin(JvmPluginDescription(
    id = "org.laolittle.plugin.CactusBot",
    name = "Cactus-Bot",
    version = "1.0",
) {
    author("LaoLittle")
    dependsOn(
        PluginDependency("org.laolittle.plugin.SkikoMirai", ">=1.0.2", true)
    )
}) {
    private val users = mutableSetOf<Long>()
    override fun onEnable() {
        init()

        logger.info { "Cactus-Bot loaded" }

        globalEventChannel().subscribeGroupMessages {
            (startsWith("原神") or startsWith(CactusConfig.botName)) Listener@{ foo ->
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
                        else sender.requireCookie { return@Listener }

                        val cookies = userData.data.cookies.takeIf { it.isNotBlank() } ?: CactusData.cookie

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
                        val userData = getUserData(sender.id)

                        sender.requireCookie { return@Listener }

                        GenshinBBSApi.getDailyNote(userData.genshinUID, userData.data.cookies, userData.data.uuid)
                            .also {
                                subject.sendMessage(it.toString())
                            }
                    }
                }
            }
        }

        globalEventChannel().subscribeFriendMessages {
            finding(Regex("原神登[录陆]")) Login@{

                messageContext {
                    send(
                        """
                    免责声明: 
                    本项目仅用作学习交流, 存储的Cookie仅用于米游社相关操作
                    如果您认可本条例, 在20s内发送"同意"即可
                    项目地址: https://github.com/LaoLittle/Cactus-Bot
                """.trimIndent()
                    )

                    receiveWithResult("同意", seconds(20)).onFailure {
                        subject.sendMessage("超时")
                        return@Login
                    }
                    delay(232)
                    send(guideMessage)
                    delay(1_320)
                    send("请发送Cookie")
                }

                val cookies = nextMessageOrNull(30_000)?.content ?: kotlin.run {
                    subject.sendMessage("超时")
                    return@Login
                }

                val gameRole = runCatching {
                    val roles = BBSApi.getRolesByCookie(cookies, GameRole.GameBiz.HK4E_CN)

                    if (roles.isEmpty()) {
                        subject.sendMessage("没有找到绑定的角色!")
                        return@Login
                    }

                    var role = roles.first()
                    cactusSuspendedTransaction {
                        val userData = getUserData(subject.id)
                        val data = userData.data
                        // roles not empty
                        if (roles.size != 1) {
                            subject.sendMessage(buildMessageChain {
                                add("查询到以下信息: \n")
                                roles.forEach { r ->
                                    add("昵称: ${r.nickname}\nuid: ${r.gameUID}\n")
                                }
                                add("请发送uid进行绑定")
                            })
                            nextMessageOrNull(60_000) { e ->
                                val uid = e.message.content.toLongOrNull() ?: return@nextMessageOrNull false
                                role = roles.firstOrNull { r -> r.gameUID == uid } ?: kotlin.run {
                                    subject.sendMessage("请确认你输入了正确的uid")
                                    return@nextMessageOrNull false
                                }
                                true
                            } ?: kotlin.run {
                                subject.sendMessage("超时")
                                return@cactusSuspendedTransaction null
                            }
                        }
                        data.cookies = cookies
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
                    旅行者${gameRole.nickname}登录成功!
                    你的UID: ${gameRole.gameUID}
                """.trimIndent()
                )
            }
        }

        globalEventChannel().subscribeMessages {
            finding(Regex("""(?:原神|${CactusConfig.botName})(.+)""")) Fun@{ result ->
                when (result.groupValues[1]) {
                    "签到" -> {
                        val userData = sender.requireCookie { return@Fun }

                        kotlin.runCatching {
                            userData.signGenshin()
                        }.onSuccess {
                            subject.sendMessage("旅行者: ${userData.genshinUID}签到成功")
                        }.onFailure {
                            subject.sendMessage("签到失败: ${it.message}")
                        }
                    }
                    "便笺" -> {
                        val userData = sender.requireCookie { return@Fun }

                        val dailyNote = kotlin.runCatching {
                            userData.getDailyNote()
                        }.getOrElse {
                            subject.sendMessage("获取失败, 原因: ${it.message}")
                            return@Fun
                        }

                        with(dailyNote) {
                            subject.sendMessage(
                                """
                            旅行者: ${userData.genshinUID}
                            体力: $currentResin / $maxResin
                            (恢复时间: $resinRecoveryTime)
                            每日委托: $finishedTask / $totalTask
                            周本奖励折扣剩余次数: $resinDiscountRemain / $resinDiscountLimit
                            派遣任务: $currentExpedition / $maxExpedition
                            日历链接: $calendarUrl
                        """.trimIndent()
                            )
                        }
                    }
                }
            }

            "开启自动签到" AutoSign@{
                sender.requireCookie {
                    return@AutoSign
                }

                if (CactusData.autoSign.add(sender.id))
                    subject.sendMessage("开启成功! ")
                else subject.sendMessage("你已经开启了自动签到")
            }
        }
    }

    override fun onDisable() {
        GenshinGachaCache.cancel()
        if (CactusConfig.autoSign)
            GenshinSignProver.cancel()
    }

    private fun init() {
        launch { getAppVersion(true) }
        // GenshinTimerProvider.start()
        CactusConfig.reload()
        CactusData.reload()
        dataFolder.mkdirs()
        gachaDataFolder.mkdir()
        avatarDataFolder.mkdir()
        cacheFolder.mkdir()

        // Services
        val nowDay = JLocalDate.now()
        val dateTime = JLocalDate.ofYearDay(nowDay.year, nowDay.dayOfYear + 1).atStartOfDay().toKotlinLocalDateTime()
        GenshinGachaCache.start(dateTime.date.atTime(4, 15))
        if (CactusConfig.autoSign)
            GenshinSignProver.start(dateTime)
    }
}