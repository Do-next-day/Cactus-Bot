package org.laolittle.plugin.genshin.service

import kotlinx.coroutines.delay
import net.mamoe.mirai.contact.User
import org.laolittle.plugin.genshin.util.getDailyNote
import org.laolittle.plugin.genshin.util.requireCookie

class GenshinResinPrompting(val user: User) : AbstractCactusService() {
    override suspend fun main() {
        val userData = user.requireCookie { return@requireCookie }

        val dailyNote = kotlin.runCatching {
            userData.getDailyNote()
        }.getOrElse { return }

        val time = dailyNote.resinRecoveryTime - 300

        if (time > 0) {
            delay(time * 1000)

        }

    }
}