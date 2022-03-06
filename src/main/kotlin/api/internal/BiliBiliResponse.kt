package org.laolittle.plugin.genshin.api.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class BiliBiliResponse(
    val code: Int? = null,
    val message: String? = null,
    val status: Boolean? = null,
    val msg: String? = null,
    val data: JsonElement? = null
) {

}