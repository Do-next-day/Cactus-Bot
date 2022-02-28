package org.laolittle.plugin.genshin.api

import io.ktor.client.request.*
import kotlinx.serialization.json.JsonObject
import org.laolittle.plugin.genshin.CactusData
import org.laolittle.plugin.genshin.api.GenshinInfo.GameRecordResponseData
import org.laolittle.plugin.genshin.api.internal.client
import org.laolittle.plugin.genshin.api.internal.setHeaders
import org.laolittle.plugin.genshin.util.Json

object GenshinBBSApi {
    const val BASE_URL = "https://api-takumi.mihoyo.com"
    const val GAME_RECORD = "$BASE_URL/game_record/app/genshin/api"

// @see https://github.com/thesadru/genshinstats/blob/master/genshinstats/errors.py
// -----------------------------------------------------------------------------
// retcode  readable description (not the response message)
// -----------------------------------------------------------------------------
//
// general
//
// 10101:   Cannot get data for more than 30 accounts per cookie per day.
// -100:    Login cookies have not been provided or are incorrect.
// 10001:   Login cookies have not been provided or are incorrect.
// 10102:   User's data is not public.
// 1009:    Could not find user; uid may not be valid.
// -1:      Internal database error, see original message.
// -10002:  Cannot get rewards info. Account has no game account bind to it.
// -108:    Language is not valid.
// 10103:   Cookies are correct but do not have a hoyolab account bound to them.
//
// code redemption
//
// -2003:   Invalid redemption code.
// -2017:   Redemption code has been claimed already.
// -2001:   Redemption code has expired.
// -2021:   Cannot claim codes for account with adventure rank lower than 10.
// -1073:   Cannot claim code. Account has no game account bound to it.
// -1071:   Login cookies from redeem_code() have not been provided or are incorrect.
//          Make sure you use account_id and cookie_token cookies.
//
// sign in
//
// -5003:   Already claimed daily reward today.
// 2001:    Already checked into hoyolab today.
//
// gacha log
//
// -100:    AuthKey is not valid. (if message is "authkey error")
//          Login cookies have not been provided or are incorrect. (if message is not "authkey error")
// -101:    AuthKey has timed-out. Update it by opening the history page in Genshin.

    suspend fun getPlayerInfo(uid: Long, cookies: String): GameRecordResponseData {
        val server = if (uid < 500000000) GenshinServer.CN_GF01
        else GenshinServer.CN_QD01

        val url = "$GAME_RECORD/index?role_id=$uid&server=$server"

        val response = Json.decodeFromString(JsonObject.serializer(), client.get(url) {
            headers.setHeaders(url, "", cookies)
        })

        return response["data"]?.let { Json.decodeFromJsonElement(GameRecordResponseData.serializer(), it) }
            ?: throw IllegalAccessException(response["message"].toString())
    }

    suspend fun getPlayerInfo(uid: Long) = getPlayerInfo(uid, CactusData.cookies)


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
