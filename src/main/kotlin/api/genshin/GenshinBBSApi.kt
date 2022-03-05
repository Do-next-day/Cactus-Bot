package org.laolittle.plugin.genshin.api.genshin

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.serializer
import org.laolittle.plugin.genshin.CactusData
import org.laolittle.plugin.genshin.api.Action.GENSHIN_SIGN
import org.laolittle.plugin.genshin.api.TAKUMI_API
import org.laolittle.plugin.genshin.api.WEB_STATIC
import org.laolittle.plugin.genshin.api.genshin.GenshinBBSApi.GenshinServer.CN_GF01
import org.laolittle.plugin.genshin.api.genshin.GenshinBBSApi.GenshinServer.CN_QD01
import org.laolittle.plugin.genshin.api.genshin.data.*
import org.laolittle.plugin.genshin.api.internal.*
import org.laolittle.plugin.genshin.util.Json
import org.laolittle.plugin.genshin.util.cacheFolder
import org.laolittle.plugin.genshin.util.decode
import org.laolittle.plugin.genshin.util.randomUUID

object GenshinBBSApi {
    /**
     * ```
     * ?role_id=&server=
     * ```
     *
     * [GENSHIN_GAME_RECORD]
     */
    private const val GENSHIN_GAME_RECORD = "$TAKUMI_API/game_record/app/genshin/api"
    private const val SIGN_API = "$TAKUMI_API/event/bbs_sign_reward"
    private const val GACHA_INFO = "$WEB_STATIC/hk4e/gacha_info"

    suspend fun getPlayerInfo(
        uid: Long,
        cookies: String = CactusData.cookie,
        uuid: String = randomUUID
    ): GenshinRecord {
        val response = getBBS(
            url = "$GENSHIN_GAME_RECORD/index?role_id=$uid&server=${getServerFromUID(uid)}",
            cookies = cookies,
            uuid = uuid
        )

        return response.getOrThrow().data.decode()
    }

    suspend fun getGachaInfo(
        server: GenshinServer,
        cookies: String = "",
        uuid: String = randomUUID,
        useCache: Boolean = true
    ): List<GachaInfo> {
        val cacheFile = cacheFolder.resolve("gacha_info_$server.json")
        if (useCache && cacheFile.isFile)
            return Json.decodeFromString(Json.serializersModule.serializer(), cacheFile.readText())

        val response = getBBS("$GACHA_INFO/$server/gacha/list.json", cookies, uuid)

        return response.getOrThrow().data["list"]!!.decode()
    }

    suspend fun getDailyNote(
        uid: Long,
        cookies: String,
        uuid: String = randomUUID
    ): DailyNote {
        val response = getBBS(
            url = "$GENSHIN_GAME_RECORD/dailyNote?role_id=$uid&server=${getServerFromUID(uid)}",
            cookies,
            uuid
        )

        return response.getOrThrow().data.decode()
    }

    /**
     * returns a JsonObject
     * not [Response]
     */
    suspend fun getGachaDetail(
        server: GenshinServer,
        cookies: String = "",
        uuid: String = randomUUID,
        gachaId: String,
        useCache: Boolean = true
    ): GachaDetail { // todo: 解析
        val cacheFile = cacheFolder.resolve("gacha_info_${server}_$gachaId.json")
        if (useCache && cacheFile.isFile)
            return Json.decodeFromString(GachaDetail.serializer(), cacheFile.readText())

        val url = "$GACHA_INFO/$server/$gachaId/zh-cn.json"

        return Json.decodeFromString(GachaDetail.serializer(), client.get(url) {
            setHeaders(url, "", cookies, uuid)
        })
    }

    suspend fun getSignInfo(
        uid: Long,
        region: GenshinServer,
        cookies: String,
        uuid: String = randomUUID
    ): SignInfo{
        val response = getBBS(
            "$SIGN_API/info?act_id=$GENSHIN_SIGN&region=$region&uid=$uid",
            cookies,
            uuid
        ).getOrThrow()

        return response.data.decode()
    }

    suspend fun getAwards(): Award{
        return getBBS("$SIGN_API/home?act_id=$GENSHIN_SIGN").getOrThrow().data.decode()
    }

    private const val SIGN_REFERRER =
        "https://webstatic.mihoyo.com/bbs/event/signin-ys/index.html?bbs_auth_required=true&act_id=$GENSHIN_SIGN&utm_source=bbs&utm_medium=mys&utm_campaign=icon"

    suspend fun signGenshin(
        genshinUID: Long,
        region: GenshinServer,
        cookies: String,
        uuid: String = randomUUID
    ): Response {
        val appVersion = "2.10.2"
        val response = postBBS(
            url = "$SIGN_API/sign",
            cookies,
            uuid,
            header = {
                set("x-rpc-app_version", appVersion)
                set("DS", getSignDS())
                set(HttpHeaders.Referrer, SIGN_REFERRER)
            },
        ) {
            userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 14_0_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) miHoYoBBS/$appVersion")
            contentType(ContentType.Application.Json)
            body = buildJsonObject {
                put("act_id", GENSHIN_SIGN)
                put("region", region.toString())
                put("uid", genshinUID)
            }.toString()
        }

        return response.getOrThrow()
    }

    suspend fun signGenshinWithAward(
        genshinUID: Long,
        region: GenshinServer,
        cookies: String,
        uuid: String = randomUUID
    ): SignResponse {
        val signInfo = getSignInfo(genshinUID, region, cookies, uuid)
        val response = if (!signInfo.isSign) signGenshin(genshinUID, region, cookies, uuid) else null
        return SignResponse(response, signInfo, CactusData.awards[signInfo.totalSignDay])
    }

    suspend fun getGameRecordCard(): String {
        val url = "https://api-takumi.mihoyo.com/game_record/app/card/wapi/getGameRecordCard"

        return client.get(url) {
            setHeaders(url, "", CactusData.cookie)
        }
    }

    fun getServerFromUID(uid: Long) = if (uid < 500000000) CN_GF01
    else CN_QD01

    @Serializable
    enum class GenshinServer {
        /**
         * 官服
         * */
        @SerialName("cn_gf01")
        CN_GF01,


        /**
         * 渠道服
         * */
        @SerialName("cn_qd01")
        CN_QD01;

        override fun toString(): String = name.lowercase()
    }
}
