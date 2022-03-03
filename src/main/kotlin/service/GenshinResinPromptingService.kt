package org.laolittle.plugin.genshin.service

import kotlinx.coroutines.delay
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.utils.error
import org.laolittle.plugin.genshin.api.internal.logger
import org.laolittle.plugin.genshin.util.getDailyNote
import org.laolittle.plugin.genshin.util.requireCookie

class GenshinResinPromptingService(private val user: User) : AbstractCactusService() {
    override suspend fun main() {
        val userData = user.requireCookie { return@requireCookie }

        val dailyNote = kotlin.runCatching {
            userData.getDailyNote()
        }.getOrElse {
            logger.error { "用户$user 获取便笺数据失败, 已停止推送" }
            return
        }

        val time = dailyNote.resinRecoveryTime - 1800

        if (time > 0) delay(time * 1000)
        else delay(160 * 8 * 60 * 1000)
        main()
    }
}