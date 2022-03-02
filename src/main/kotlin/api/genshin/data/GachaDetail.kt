package org.laolittle.plugin.genshin.api.genshin.data

import kotlinx.serialization.Serializable

@Serializable
data class GachaDetail(
    val banner: String,
    val content: String,
)