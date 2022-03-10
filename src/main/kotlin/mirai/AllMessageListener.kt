package icu.dnddl.plugin.genshin.mirai

import icu.dnddl.plugin.genshin.CactusConfig
import icu.dnddl.plugin.genshin.CactusData
import icu.dnddl.plugin.genshin.database.UserSetting
import icu.dnddl.plugin.genshin.draw.infoImage
import icu.dnddl.plugin.genshin.service.AbstractCactusService
import icu.dnddl.plugin.genshin.util.buildSuccessMessage
import icu.dnddl.plugin.genshin.util.getDailyNote
import icu.dnddl.plugin.genshin.util.requireCookie
import icu.dnddl.plugin.genshin.util.signGenshin
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeMessages
import org.laolittle.plugin.sendImage

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
                            subject.sendMessage(it.buildSuccessMessage(userData.genshinUID))
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

                        subject.sendImage(dailyNote.infoImage())

                        /**
                         * with(dailyNote) {
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
                         */
                    }
                }
            }

            finding(Regex("(.*)自动签到$")) AutoSign@{
                sender.requireCookie { return@AutoSign }
                val autoSign = CactusData.userSetting.getOrPut(sender.id) {
                    UserSetting(pushSubject = sender.id)
                }::autoSign

                when (it.groupValues[1]) {
                    "开启" -> {
                        if (!autoSign.get()) {
                            autoSign.set(true)
                            subject.sendMessage("已开启自动签到")
                        } else subject.sendMessage("你已经开启了自动签到, 无需重复开启")
                    }
                    "关闭" -> {
                        if (autoSign.get()) {
                            autoSign.set(false)
                            subject.sendMessage("已关闭自动签到")
                        } else subject.sendMessage("你尚未开启自动签到")
                    }
                    else -> {
                        subject.sendMessage(
                            """
                            发送 "开启自动签到", 每天会自动帮你进行米游社签到
                            发送 "关闭自动签到" 即可关闭
                            你现在是${if (autoSign.get()) "开启" else "关闭"}的状态
                        """.trimIndent()
                        )
                    }
                }
            }
        }
    }
}