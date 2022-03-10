package icu.dnddl.plugin.genshin.service

import icu.dnddl.plugin.genshin.CactusBot
import icu.dnddl.plugin.genshin.CactusData.biliSubscribes
import icu.dnddl.plugin.genshin.api.bilibili.BiliBiliApi.getNewDynamic

object BiliBiliDynamicTasker : AbstractCactusTimerService(
    serviceName = "BiliBiliDynamic",
) {
    private val logger get() = CactusBot.logger
    private val subscribes get() = biliSubscribes

    override suspend fun main() {
        subscribes.forEach { (buid, subData) ->
            val dynamics = getNewDynamic(buid).dynamics?.filter { it.describe.dynamictime > subData.last }
            dynamics?.forEach {
                it.card
            }

        }
    }

}