package icu.dnddl.plugin.genshin.api.mihoyobbs

import icu.dnddl.plugin.genshin.api.BBS_API_BASE
import icu.dnddl.plugin.genshin.api.TAKUMI_API
import icu.dnddl.plugin.genshin.api.internal.getBBS
import icu.dnddl.plugin.genshin.api.mihoyobbs.data.GameRole
import icu.dnddl.plugin.genshin.util.decode
import kotlinx.serialization.json.JsonObject

object BBSApi {
    private const val USER_FULL_INFO = "$BBS_API_BASE/user/wapi/getUserFullInfo"
    private const val ROLE_URL = "$TAKUMI_API/binding/api/getUserGameRolesByCookie"

    suspend fun getUserInfo(cookies: String): JsonObject {
        val response = getBBS(
            url = USER_FULL_INFO,
            cookies = cookies
        )
        return if (response.isSuccess) response.data // TODO: 2022/3/1 替换成UserInfo
        else throw response.cause
    }

    suspend fun getRolesByCookie(cookies: String, type: GameRole.GameBiz): List<GameRole> {
        val response = getBBS(
            url = "${ROLE_URL}?game_biz=$type",
            cookies = cookies
        )
        return response.getOrThrow().data["list"]!!.decode()
    }
}