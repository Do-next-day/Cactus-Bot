package org.laolittle.plugin.genshin.mirai

import kotlinx.coroutines.delay
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.event.whileSelectMessages
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.message.nextMessageOrNull
import org.laolittle.plugin.genshin.CactusConfig.guideMessage
import org.laolittle.plugin.genshin.api.bbs.BBSApi
import org.laolittle.plugin.genshin.api.bbs.data.GameRole
import org.laolittle.plugin.genshin.database.UserSetting
import org.laolittle.plugin.genshin.database.cactusSuspendedTransaction
import org.laolittle.plugin.genshin.service.AbstractCactusService
import org.laolittle.plugin.genshin.util.requireCookie
import org.laolittle.plugin.genshin.util.seconds
import org.laolittle.plugin.genshin.util.userSettings

object FriendMessageListener : AbstractCactusService() {
    override suspend fun main() {
        globalEventChannel().subscribeFriendMessages {
            "原神设置" Setting@{
                sender.requireCookie { return@Setting }

                val setting = userSettings.getOrPut(sender.id) { UserSetting(pushSubject = sender.id) }

                subject.sendMessage("""
                    修改推送对象: 
                    推送到+群号 或 推送到这里 (好友推送)
                    树脂多少分钟满提醒 (默认: 30分钟)
                    数字+提醒我
                """.trimIndent())

                var keep = false
                whileSelectMessages {
                    startsWith("") {
                        true
                    }

                    "" {
                        true
                    }

                    "结束设置" {

                        false
                    }

                    timeout(30_000) {
                        val foo = keep
                        if (!foo) subject.sendMessage("设置结束")
                        else keep = false
                        foo
                    }
                }
            }

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
                    send(subject.guideMessage)
                    delay(1_320)
                    send("请在5分钟内内发送Cookie")
                }

                val cookies = nextMessageOrNull(300_000)?.content ?: kotlin.run {
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
                        val userData = org.laolittle.plugin.genshin.database.getUserData(subject.id)
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

                userSettings.putIfAbsent(sender.id, UserSetting(pushSubject = sender.id))
                subject.sendMessage(
                    """
                    旅行者${gameRole.nickname}登录成功!
                    你的UID: ${gameRole.gameUID}
                    发送“原神设置”即可自定义设置
                """.trimIndent()
                )
            }
        }
    }
}