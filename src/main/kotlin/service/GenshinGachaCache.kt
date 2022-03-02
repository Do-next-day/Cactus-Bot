package org.laolittle.plugin.genshin.service

import kotlinx.coroutines.delay
import kotlinx.serialization.serializer
import org.laolittle.plugin.genshin.api.genshin.GenshinBBSApi
import org.laolittle.plugin.genshin.util.Json
import org.laolittle.plugin.genshin.util.cacheFolder

object GenshinGachaCache : CactusService(type = Type.Task) {
    override suspend fun main() {
        while (isActive) {
            val cache: suspend (GenshinBBSApi.GenshinServer) -> Unit = { s ->
                Json.encodeToString(
                    Json.serializersModule.serializer(), GenshinBBSApi.getGachaInfo(
                        server = s,
                        useCache = false
                    )
                ).also {
                    cacheFolder.resolve("gacha_info_$s.json").writeText(it)
                }
            }

            cache(GenshinBBSApi.GenshinServer.CN_GF01)
            cache(GenshinBBSApi.GenshinServer.CN_QD01)

            delay(aDay)
        }
    }
}