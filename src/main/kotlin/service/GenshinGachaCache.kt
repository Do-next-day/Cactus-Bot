package org.laolittle.plugin.genshin.service

import kotlinx.coroutines.delay
import kotlinx.serialization.serializer
import org.laolittle.plugin.genshin.api.genshin.GenshinBBSApi
import org.laolittle.plugin.genshin.util.Json
import org.laolittle.plugin.genshin.util.cacheFolder

object GenshinGachaCache : AbstractCactusTimerService(
    serviceName = "GachaCache",
) {
    override suspend fun main() {
        cacheGachaServer(GenshinBBSApi.GenshinServer.CN_GF01)
    }

    private suspend fun cacheGachaServer(server: GenshinBBSApi.GenshinServer) {
        Json.encodeToString(
            Json.serializersModule.serializer(), GenshinBBSApi.getGachaInfo(
                server = server,
                useCache = false
            )
        ).also {
            cacheFolder.resolve("gacha_info_$server.json").writeText(it)
        }
    }
}