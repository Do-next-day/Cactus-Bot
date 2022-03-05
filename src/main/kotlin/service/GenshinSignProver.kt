package org.laolittle.plugin.genshin.service

import kotlinx.coroutines.delay
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.ConsoleCommandSender.subject
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.utils.verbose
import org.laolittle.plugin.genshin.CactusBot
import org.laolittle.plugin.genshin.CactusData
import org.laolittle.plugin.genshin.database.User
import org.laolittle.plugin.genshin.database.Users
import org.laolittle.plugin.genshin.database.cactusSuspendedTransaction
import org.laolittle.plugin.genshin.util.buildSuccessMessage
import org.laolittle.plugin.genshin.util.signGenshin
import kotlin.random.Random

object GenshinSignProver : AbstractCactusTimerService(
    serviceName = "GenshinSign",
) {
    private val logger get() = CactusBot.logger
    private val autoSign: Set<Long> get() {
        val foo = mutableSetOf<Long>()
        CactusData.userSetting.forEach { (userId, setting) ->
            if (setting.autoSign) foo.add(userId)
        }
        return foo
    }
    override suspend fun main() {
        cactusSuspendedTransaction {
            User.find { Users.id inList autoSign }.forEach { userData ->
                delay(3_000)
                var friend: Friend? = null
                for (bot in Bot.instances) {
                    friend = bot.getFriend(userData.id.value) ?: continue
                    break
                }
                if (userData.data.cookies.isNotBlank()) {
                    logger.verbose { "开始执行用户${userData.id.value} (${userData.genshinUID})的签到" }
                    runCatching {
                        userData.signGenshin()
                    }.onSuccess {
                        friend?.sendMessage(it.buildSuccessMessage(userData.genshinUID))
                    }.getOrElse { e ->
                        friend?.sendMessage("签到失败！原因: ${e.message}")
                        //if (e is ApiAccessDeniedException && e.restCode == 0)
                        return@forEach
                    }
                }

                delay(Random.nextLong(10_000, 30_000))
            }
        }

        delay(aDay + Random.nextLong(aDay shr 4))
    }
}