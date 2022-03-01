package org.laolittle.plugin.genshin.service

import kotlinx.coroutines.delay
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Friend
import org.laolittle.plugin.genshin.CactusData
import org.laolittle.plugin.genshin.database.User
import org.laolittle.plugin.genshin.database.Users
import org.laolittle.plugin.genshin.util.signGenshin
import kotlin.random.Random

object GenshinSignProver : CactusService() {
    override suspend fun main() {
        while (true) {
            User.find { Users.id inList CactusData.autoSign }
                .forEach { userData ->
                    delay(3_000)
                    var friend: Friend? = null
                    for (bot in Bot.instances) {
                        friend = bot.getFriend(userData.id.value) ?: continue
                        break
                    }
                    if (userData.data.cookies.isNotBlank()) {
                        runCatching {
                            userData.signGenshin().getOrThrow()
                        }.onSuccess {
                            friend?.sendMessage("旅行者${userData.genshinUID}签到成功！")
                        }.getOrElse { e ->
                            friend?.sendMessage("签到失败！原因: ${e.message}")
                            return@forEach
                        }
                    }

                    delay(Random.nextLong(10_000, 30_000))
                }

            delay((aDay shr 1) + Random.nextLong(aDay shr 1))
        }
    }
}