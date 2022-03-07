package icu.dnddl.plugin.genshin.api.internal

import icu.dnddl.plugin.genshin.api.genshin.data.Award
import icu.dnddl.plugin.genshin.api.genshin.data.SignInfo

data class SignResponse(
    val response: MiHoYoBBSResponse? = null,
    val signInfo: SignInfo,
    val award: Award.AwardItem
)