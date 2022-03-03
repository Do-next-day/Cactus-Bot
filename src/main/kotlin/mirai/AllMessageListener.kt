package org.laolittle.plugin.genshin.mirai

import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeMessages
import org.laolittle.plugin.genshin.CactusConfig
import org.laolittle.plugin.genshin.CactusData
import org.laolittle.plugin.genshin.database.UserSetting
import org.laolittle.plugin.genshin.service.AbstractCactusService
import org.laolittle.plugin.genshin.util.getDailyNote
import org.laolittle.plugin.genshin.util.requireCookie
import org.laolittle.plugin.genshin.util.signGenshin

object AllMessageListener : AbstractCactusService() {
    override suspend fun main() {
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

            finding(Regex("(.*)自动签到")) AutoSign@{
                sender.requireCookie { return@AutoSign }
                val autoSign = CactusData.userSetting.getOrPut(sender.id) {
                    UserSetting(pushSubject = sender.id)
                }::autoSign

                when (it.groupValues[1]) {
                    "开启" -> {
                        if (!autoSign.get()) {
                            autoSign.set(true)
                            subject.sendMessage("已开启自动签到")
                        } else subject.sendMessage("你已经开启了自动签到")
                    }
                    "关闭" -> {
                        if (autoSign.get()) {
                            autoSign.set(false)
                            subject.sendMessage("已关闭自动签到")
                        } else subject.sendMessage("你未开启自动签到")
                    }
                    else -> {
                        subject.sendMessage(
                            """
                            发送 "开启自动签到", 每天都会自动帮你进行米游社签到
                            发送 "关闭自动签到" 即可关闭
                        """.trimIndent()
                        )
                    }
                }
            }
        }
    }
}