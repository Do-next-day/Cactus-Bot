package org.laolittle.plugin.genshin.api.internal

import org.laolittle.plugin.genshin.api.genshin.data.AwardInfo
import org.laolittle.plugin.genshin.api.genshin.data.SignInfo

data class SignResponse(
    val response: Response? = null,
    val signInfo: SignInfo,
    val award: AwardInfo.AwardItem
)