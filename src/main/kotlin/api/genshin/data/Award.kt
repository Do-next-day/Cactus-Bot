package icu.dnddl.plugin.genshin.api.genshin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Award(
    val month: Int,
    val awards: List<AwardItem>
) {
    @Serializable
    data class AwardItem(
        val icon: String,
        val name: String,
        @SerialName("cnt")
        val count: Int
    )
}