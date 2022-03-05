package org.laolittle.plugin.genshin.service

import kotlinx.serialization.serializer
import org.laolittle.plugin.genshin.CactusData
import org.laolittle.plugin.genshin.api.genshin.GenshinBBSApi
import org.laolittle.plugin.genshin.util.Json
import org.laolittle.plugin.genshin.util.cacheFolder
import java.time.LocalDate

object GenshinSignAwardsCache : AbstractCactusTimerService(
    serviceName = "AwardsCache",
) {
    override suspend fun main() {
        if (CactusData.awardMonth != LocalDate.now().monthValue){
            val awards = GenshinBBSApi.getAwards()
            CactusData.awardMonth = awards.month
            CactusData.awards = awards.awards

            Json.encodeToString(
                Json.serializersModule.serializer(),
                awards
            ).also {
                cacheFolder.resolve("awards.json").writeText(it)
            }
        }
    }
}