package icu.dnddl.plugin.genshin.api.mihoyobbs

import icu.dnddl.plugin.genshin.CactusData
import icu.dnddl.plugin.genshin.api.BBS_API_BASE
import icu.dnddl.plugin.genshin.api.GAME_RECORD
import icu.dnddl.plugin.genshin.api.TAKUMI_API
import icu.dnddl.plugin.genshin.api.internal.MiHoYoBBSResponse
import icu.dnddl.plugin.genshin.api.internal.getBBS
import icu.dnddl.plugin.genshin.api.mihoyobbs.data.GameRole
import icu.dnddl.plugin.genshin.util.decode
import icu.dnddl.plugin.genshin.util.randomUUID
import kotlinx.serialization.json.JsonObject

object BBSApi {
    private const val USER_FULL_INFO = "$BBS_API_BASE/user/wapi/getUserFullInfo"
    private const val ROLE_URL = "$TAKUMI_API/binding/api/getUserGameRolesByCookie"
    private const val RECORD_CARD = "$GAME_RECORD/card/wapi/getGameRecordCard"

    suspend fun getUserInfo(cookies: String): JsonObject {
        val response = getBBS(
            url = USER_FULL_INFO,
            cookies = cookies
        )
        return if (response.isSuccess) response.data // TODO: 2022/3/1 替换成UserInfo
        else throw response.cause
    }

    suspend fun getGameRecordCard(
        uid: Long,
        cookies: String = CactusData.cookie,
        uuid: String = randomUUID,
    ): MiHoYoBBSResponse { //todo 解析
        val url = "$RECORD_CARD?uid=$uid"

        return getBBS(url, cookies, uuid)
    }

    suspend fun getRolesByCookie(cookies: String, type: GameRole.GameBiz): List<GameRole> {
        val response = getBBS(
            url = "${ROLE_URL}?game_biz=$type",
            cookies = cookies
        )
        return response.getOrThrow().data["list"]!!.decode()
    }
}