package org.laolittle.plugin.genshin.api.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import org.laolittle.plugin.genshin.api.ApiAccessDeniedException
import java.rmi.UnexpectedException

///////////////////////////////////////////////////////////////////////////
//// @see https://github.com/thesadru/genshinstats/blob/master/genshinstats/errors.py
//// -----------------------------------------------------------------------------
//// retcode  readable description (not the response message)
//// -----------------------------------------------------------------------------
////
//// general
////
//// 10101:   Cannot get data for more than 30 accounts per cookie per day.
//// -100:    Login cookies have not been provided or are incorrect.
//// 10001:   Login cookies have not been provided or are incorrect.
//// 10102:   User's data is not public.
//// 1009:    Could not find user; uid may not be valid.
//// -1:      Internal database error, see original message.
//// -10002:  Cannot get rewards info. Account has no game account bind to it.
//// -108:    Language is not valid.
//// 10103:   Cookies are correct but do not have a hoyolab account bound to them.
////
//// code redemption
////
//// -2003:   Invalid redemption code.
//// -2017:   Redemption code has been claimed already.
//// -2001:   Redemption code has expired.
//// -2021:   Cannot claim codes for account with adventure rank lower than 10.
//// -1073:   Cannot claim code. Account has no game account bound to it.
//// -1071:   Login cookies from redeem_code() have not been provided or are incorrect.
////          Make sure you use account_id and cookie_token cookies.
////
//// clock in
////
//// -5003:   Already claimed daily reward today.
//// 2001:    Already checked into hoyolab today.
////
//// gacha log
////
//// -100:    AuthKey is not valid. (if message is "authkey error")
////          Login cookies have not been provided or are incorrect. (if message is not "authkey error")
//// -101:    AuthKey has timed-out. Update it by opening the history page in Genshin.
///////////////////////////////////////////////////////////////////////////

@Serializable
data class MiHoYoBBSResponse(
    @SerialName("retcode") val restCode: Int,
    val message: String,
    @SerialName("data") private val originData: JsonObject?,
) {
    val isSuccess get() = restCode == 0

    val data: JsonObject
        get() = when (restCode) {
            0 -> originData ?: throw UnexpectedException("服务器返回数据有误! $message")
            else -> if (originData.isNullOrEmpty()) throw cause else originData
        }

    val cause
        get() = ApiAccessDeniedException(
            when (restCode) {
                0 -> null
                10101 -> "当前账号无法继续查询"
                -100, 10001 -> "Cookie有误"
                10102 -> "用户数据未公开"
                1009 -> "无法找到用户"
                -10002 -> "当前账号无绑定游戏"
                -108 -> "语言错误"
                10103 -> "无绑定米游社账号 $message"
                else -> message
            },
            code = restCode
        )

    fun getOrThrow() = if (isSuccess) this else throw cause
}