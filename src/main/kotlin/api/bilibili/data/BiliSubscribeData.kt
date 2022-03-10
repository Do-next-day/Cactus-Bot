package icu.dnddl.plugin.genshin.api.bilibili.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class BiliSubscribeData(
    @SerialName("name")
    val name: String,
    @SerialName("color")
    var color: String = "#d3edfa",
    @SerialName("last")
    var last: Long = Instant.now().epochSecond,
    @SerialName("lastLive")
    var lastLive: Long = Instant.now().epochSecond,
    @SerialName("contacts")
    val contacts: MutableMap<Long, String> = mutableMapOf(),
    @SerialName("filter")
    val filter: MutableMap<Long, MutableList<String>> = mutableMapOf(),
    @SerialName("containFilter")
    val containFilter: MutableMap<Long, MutableList<String>> = mutableMapOf()
)
