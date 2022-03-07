package icu.dnddl.plugin.genshin.service

import icu.dnddl.plugin.genshin.CactusBot
import icu.dnddl.plugin.genshin.CactusData
import icu.dnddl.plugin.genshin.api.genshin.GenshinBBSApi
import icu.dnddl.plugin.genshin.database.User
import icu.dnddl.plugin.genshin.database.UserSetting
import icu.dnddl.plugin.genshin.database.Users
import icu.dnddl.plugin.genshin.database.cactusSuspendedTransaction
import icu.dnddl.plugin.genshin.mirai.getSubjectFromBots
import icu.dnddl.plugin.genshin.util.*
import kotlinx.coroutines.delay
import kotlinx.serialization.serializer
import net.mamoe.mirai.utils.verbose
import kotlin.random.Random

object GenshinSignProver : AbstractCactusTimerService(
    serviceName = "GenshinSign",
) {
    private val logger get() = CactusBot.logger
    private val autoSign: Set<Long>
        get() {
            val foo = mutableSetOf<Long>()
            userSettings.forEach { (userId, setting) ->
                if (setting.autoSign) foo.add(userId)
            }
            return foo
        }

    override suspend fun main() {
        val awards = GenshinBBSApi.getAwards()
        CactusData.awardMonth = awards.month
        CactusData.awards = awards.awards

        Json.encodeToString(
            Json.serializersModule.serializer(),
            awards
        ).also {
            cacheFolder.resolve("awards.json").writeText(it)
        }
        cactusSuspendedTransaction {
            User.find { Users.id inList autoSign }.forEach { userData ->
                delay(3_000)
                val pushSubject = (CactusData.userSetting[userData.id.value]
                    ?: UserSetting(pushSubject = userData.id.value)).pushSubject
                val subject = getSubjectFromBots(pushSubject)
                if (userData.data.cookies.isNotBlank()) {
                    logger.verbose { "开始执行用户${userData.id.value} (${userData.genshinUID})的签到" }
                    runCatching {
                        userData.signGenshin()
                    }.onSuccess {
                        subject?.sendMessage(it.buildSuccessMessage(userData.genshinUID))
                    }.getOrElse { e ->
                        subject?.sendMessage("签到失败！原因: ${e.message}")
                        //if (e is ApiAccessDeniedException && e.restCode == 0)
                        return@forEach
                    }
                }

                delay(Random.nextLong(10_000, 30_000))
            }
        }
    }
}