package org.laolittle.plugin.genshin.service

import kotlinx.coroutines.delay
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.error
import org.laolittle.plugin.genshin.api.internal.logger
import org.laolittle.plugin.genshin.mirai.getSubjectFromBots
import org.laolittle.plugin.genshin.util.getDailyNote
import org.laolittle.plugin.genshin.util.requireCookie
import org.laolittle.plugin.genshin.util.userSettings

class GenshinResinPromptingService(private val user: User) : AbstractCactusService() {
    override suspend fun main() {
        val userData = user.requireCookie { return@requireCookie }

        val dailyNote = kotlin.runCatching {
            userData.getDailyNote()
        }.getOrElse {
            logger.error { "用户$user 获取便笺数据失败, 已停止推送。原因: ${it.message}" }
            return
        }

        val time = dailyNote.resinRecoveryTime - 1800

        if (time >= 0) delay(time * 1000)
        else delay(156 * 8 * 60 * 1000)

        val pushSubject = userSettings[user.id]?.pushSubject ?: throw IllegalStateException("无法找到用户设置！")
        getSubjectFromBots(pushSubject)?.sendMessage(
            buildMessageChain {
                +At(user)
                +"您的树脂还有半小时补满, 请及时上线清理哦! "
            })

        main()
    }
}