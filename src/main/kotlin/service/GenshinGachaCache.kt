package icu.dnddl.plugin.genshin.service

import icu.dnddl.plugin.genshin.api.genshin.GenshinBBSApi
import icu.dnddl.plugin.genshin.util.Json
import icu.dnddl.plugin.genshin.util.cacheFolder
import kotlinx.serialization.serializer

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