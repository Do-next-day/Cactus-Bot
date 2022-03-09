package icu.dnddl.plugin.genshin.service

import icu.dnddl.plugin.genshin.api.bilibili.BiliBiliApi.getNewDynamic

object BiliBiliDynamicTasker : AbstractCactusTimerService(
    serviceName = "BiliBiliDynamic",
) {

    override suspend fun main() {
        getNewDynamic(401742377)
    }

}