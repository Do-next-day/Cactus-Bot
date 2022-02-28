package org.laolittle.plugin.genshin.api.bbs

import kotlinx.serialization.json.decodeFromJsonElement
import org.laolittle.plugin.genshin.api.BBS_API_BASE
import org.laolittle.plugin.genshin.api.internal.getBBS
import org.laolittle.plugin.genshin.util.Json

object BBSApi {
    const val USER_FULL_INFO = "$BBS_API_BASE/user/wapi/getUserFullInfo"
    const val ROLE_URL = "https://api-takumi.mihoyo.com/binding/api/getUserGameRolesByCookie?game_biz="

    suspend fun getUserInfo(cookies: String) =
        getBBS(
            url = USER_FULL_INFO,
            cookies = cookies
        ).data

    suspend fun getRolesByCookie(cookies: String, type: BBSData.GameBiz): List<BBSData.GameRole> {
        val response = getBBS(
            url = "${ROLE_URL}$type",
            cookies = cookies
        )
        return Json.decodeFromJsonElement(response.data["list"]!!)
    }
}