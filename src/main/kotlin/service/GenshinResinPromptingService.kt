package icu.dnddl.plugin.genshin.service

import icu.dnddl.plugin.genshin.api.internal.logger
import icu.dnddl.plugin.genshin.mirai.getSubjectFromBots
import icu.dnddl.plugin.genshin.util.getDailyNote
import icu.dnddl.plugin.genshin.util.requireCookie
import icu.dnddl.plugin.genshin.util.userSettings
import kotlinx.coroutines.delay
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.error

class GenshinResinPromptingService(private val user: User) : AbstractCactusService() {
    private var repeat = 0
    override suspend fun main() {
        while (isActive) {
            val userData = user.requireCookie { return@requireCookie }

            val dailyNote = kotlin.runCatching {
                userData.getDailyNote()
            }.getOrElse {
                logger.error { "用户$user 获取便笺数据失败, 已停止推送。原因: ${it.message}" }
                return
            }

            val time = dailyNote.resinRecoveryTime - 1800

            repeat++
            if (time >= 0) delay(time * 1000)
            else {
                delay(156 * 8 * 60 * 1000)
                continue
            }

            val pushSubject = userSettings[user.id]?.pushSubject ?: throw IllegalStateException("无法找到用户设置！")
            getSubjectFromBots(pushSubject)?.sendMessage(
                buildMessageChain {
                    +At(user)
                    +"您的树脂还有半小时补满, 请及时上线清理哦! "
                })
        }
    }
}