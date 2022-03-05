package org.laolittle.plugin.genshin.api.genshin.data

import kotlinx.serialization.Serializable

@Serializable
data class GachaDetail(
    val banner: String,
    val content: String,
) // TODO: 2022/3/5 解析