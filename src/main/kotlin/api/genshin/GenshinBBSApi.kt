package org.laolittle.plugin.genshin.api.genshin

import io.ktor.client.request.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.laolittle.plugin.genshin.CactusData
import org.laolittle.plugin.genshin.api.ACT_ID_GENSHIN
import org.laolittle.plugin.genshin.api.TAKUMI_URL
import org.laolittle.plugin.genshin.api.genshin.GenshinBBSApi.GenshinServer.CN_GF01
import org.laolittle.plugin.genshin.api.genshin.GenshinBBSApi.GenshinServer.CN_QD01
import org.laolittle.plugin.genshin.api.genshin.GenshinData.GenshinRecordResponse
import org.laolittle.plugin.genshin.api.internal.client
import org.laolittle.plugin.genshin.api.internal.getBBS
import org.laolittle.plugin.genshin.api.internal.postBBS
import org.laolittle.plugin.genshin.api.internal.setHeaders
import org.laolittle.plugin.genshin.util.Json
import org.laolittle.plugin.genshin.util.randomUUID

object GenshinBBSApi {
    const val GENSHIN_GAME_RECORD = "$TAKUMI_URL/game_record/app/genshin/api"
    const val SIGN_URL = "$TAKUMI_URL/event/bbs_sign_reward/sign"

    suspend fun getPlayerInfo(uid: Long, cookies: String = CactusData.cookies): GenshinRecordResponse {
        val server = if (uid < 500000000) CN_GF01
        else CN_QD01

        val response = getBBS(
            url = "$GENSHIN_GAME_RECORD/index?role_id=$uid&server=$server",
            cookies = cookies
        )

        return if (response.isSuccess) Json.decodeFromJsonElement(
            GenshinRecordResponse.serializer(), response.data
        )
        else throw IllegalAccessException(response.cause)
    }

    suspend fun signGenshin(genshinUID: Long, region: GenshinServer, cookies: String, uuid: String = randomUUID): String {
        val response = postBBS(
            url = SIGN_URL,
            cookies,
            uuid
        ) {
            body = buildJsonObject {
                put("act_id", ACT_ID_GENSHIN)
                put("region", region.toString())
                put("uid", genshinUID)
            }.toString()
        }
        println(response.toString())
        return """
            旅行者: ${genshinUID} 签到成功
        """.trimIndent()
    }

    suspend fun getGameRecordCard(): String {
        val url = "https://api-takumi.mihoyo.com/game_record/app/card/wapi/getGameRecordCard"

        return client.get(url) {
            setHeaders(url, "", CactusData.cookies)
        }
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

        override fun toString(): String = name.lowercase()
    }
}
