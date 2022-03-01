package org.laolittle.plugin.genshin.api.genshin

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.laolittle.plugin.genshin.CactusData
import org.laolittle.plugin.genshin.api.Action.GENSHIN_SIGN
import org.laolittle.plugin.genshin.api.ApiFailedAccessException
import org.laolittle.plugin.genshin.api.TAKUMI_URL
import org.laolittle.plugin.genshin.api.genshin.GenshinBBSApi.GenshinServer.CN_GF01
import org.laolittle.plugin.genshin.api.genshin.GenshinBBSApi.GenshinServer.CN_QD01
import org.laolittle.plugin.genshin.api.genshin.GenshinData.GenshinRecordResponse
import org.laolittle.plugin.genshin.api.internal.*
import org.laolittle.plugin.genshin.util.Json
import org.laolittle.plugin.genshin.util.randomUUID

object GenshinBBSApi {
    const val GENSHIN_GAME_RECORD = "$TAKUMI_URL/game_record/app/genshin/api"
    const val SIGN_URL = "$TAKUMI_URL/event/bbs_sign_reward/sign"

    suspend fun getPlayerInfo(uid: Long, cookies: String = CactusData.cookies, uuid: String = randomUUID): GenshinRecordResponse {
        val server = if (uid < 500000000) CN_GF01
        else CN_QD01

        val response = getBBS(
            url = "$GENSHIN_GAME_RECORD/index?role_id=$uid&server=$server",
            cookies = cookies,
            uuid = uuid
        )

        return if (response.isSuccess) Json.decodeFromJsonElement(
            GenshinRecordResponse.serializer(), response.data
        )
        else throw response.cause
    }

    private const val SIGN_REFERRER =
        "https://webstatic.mihoyo.com/bbs/event/signin-ys/index.html?bbs_auth_required=true&act_id=$GENSHIN_SIGN&utm_source=bbs&utm_medium=mys&utm_campaign=icon"

    suspend fun signGenshin(
        genshinUID: Long,
        region: GenshinServer,
        cookies: String,
        uuid: String = randomUUID
    ): Response {
        val response = postBBS(
            url = SIGN_URL,
            cookies,
            uuid
        ) {
            val appVersion = "2.10.2"
            userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 14_0_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) miHoYoBBS/$appVersion")
            contentType(ContentType.Application.Json)
            headers["x-rpc-app_version"] = appVersion
            headers["DS"] = getSignDS()
            headers[HttpHeaders.Referrer] = SIGN_REFERRER
            body = buildJsonObject {
                put("act_id", GENSHIN_SIGN)
                put("region", region.toString())
                put("uid", genshinUID)
            }.toString()
        }

        return if (response.isSuccess) response
        else throw ApiFailedAccessException(response.message)
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
