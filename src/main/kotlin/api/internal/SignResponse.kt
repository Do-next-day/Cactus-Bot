package org.laolittle.plugin.genshin.api.internal

import org.laolittle.plugin.genshin.api.genshin.data.Award
import org.laolittle.plugin.genshin.api.genshin.data.SignInfo

data class SignResponse(
    val response: MiHoYoBBSResponse? = null,
    val signInfo: SignInfo,
    val award: Award.AwardItem
)