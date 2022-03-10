package icu.dnddl.plugin.genshin.service

import icu.dnddl.plugin.genshin.api.internal.logger
import icu.dnddl.plugin.genshin.database.User
import icu.dnddl.plugin.genshin.mirai.getSubjectFromBots
import icu.dnddl.plugin.genshin.util.getDailyNote
import icu.dnddl.plugin.genshin.util.userSettings
import kotlinx.coroutines.delay
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.error

class GenshinResinPromptingService(private val user: User) : AbstractCactusService() {
    private var repeat = 0
    override suspend fun main() {
        while (isActive) {
            val userData = user.data
            if (userData.cookies.isBlank()) return

            val dailyNote = kotlin.runCatching {
                user.getDailyNote()
            }.getOrElse {
                logger.error { "用户${user.genshinUID} (QQ: ${user.id}) 获取便笺数据失败, 已停止推送。原因: ${it.message}" }
                return
            }

            val time = dailyNote.resinRecoveryTime - 1800

            repeat++
            if (time >= 0) delay(time * 1000)
            else {
                delay(156 * 8 * 60 * 1000)
                continue
            }

            val pushSubject = userSettings[user.id.value]?.pushSubject ?: throw IllegalStateException("无法找到用户设置！")
            getSubjectFromBots(pushSubject)?.sendMessage(
                buildMessageChain {
                    +At(user.id.value)
                    +"您的树脂还有半小时补满, 请及时上线清理哦! "
                })
        }
    }
}