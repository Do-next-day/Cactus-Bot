package org.laolittle.plugin.genshin.api

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import org.laolittle.plugin.genshin.Data
import org.laolittle.plugin.genshin.api.GenshinInfo.GameRecordResponseData
import org.laolittle.plugin.genshin.api.internal.BBS_APP_VER
import org.laolittle.plugin.genshin.api.internal.client
import org.laolittle.plugin.genshin.api.internal.getDS
import org.laolittle.plugin.genshin.database.Json

object GenshinApi {
    const val BASE_URL = "https://api-takumi.mihoyo.com"
    const val GAME_RECORD = "$BASE_URL/game_record/app/genshin/api"

    suspend fun getPlayerInfo(uid: Long): GameRecordResponseData {
        val server = if (uid < 500000000) GenshinServer.CN_GF01
        else GenshinServer.CN_QD01

        val url = "$GAME_RECORD/index?role_id=$uid&server=$server"

        val response = Json.decodeFromString(JsonObject.serializer(), client.get(url) {
            headers.setHeaders(url, "", Data.cookies)
        })

        return response["data"]?.let { Json.decodeFromJsonElement(it) }
            ?: throw IllegalAccessException(response["message"].toString())
    }

    private fun HeadersBuilder.setHeaders(url: String, body: String, cookies: String) = apply {
        append("x-rpc-app_version", BBS_APP_VER)
        append("x-rpc-client_type", "5")
        append("DS", getDS(url, body))
        set("Cookie", cookies)
    }


    enum class GenshinServer {
        /**
         * 官服
         * */
        CN_GF01,


        /**
         * 渠道服
         * */
        CN_QD01;

        override fun toString(): String {
            return name.lowercase()
        }
    }
}
