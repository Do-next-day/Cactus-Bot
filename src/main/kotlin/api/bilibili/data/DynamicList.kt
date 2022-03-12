package icu.dnddl.plugin.genshin.api.bilibili.data

import icu.dnddl.plugin.genshin.util.getDynamicTimeFromID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * 动态列表
 *
 * @param dynamics 动态列表
 * @property hasMore 是否有更多动态
 * @param nextOffset 下一个动态偏移
 * @author Colter23
 */
@Serializable
data class DynamicList(
    @SerialName("cards")
    val dynamics: List<DynamicInfo>? = null,
    @SerialName("has_more")
    private val more: Int = 0,
    @SerialName("next_offset")
    val nextOffset: Long? = null
) {
    val hasMore get() = more == 1
}

/**
 *
 */
@Serializable
data class DynamicInfo(
    @SerialName("card")
    val card: String,
    @SerialName("desc")
    val describe: DynamicDescribe,
    @SerialName("display")
    val display: DynamicDisplay,
)

@Serializable
data class DynamicDescribe(
    @SerialName("uid")
    val uid: Long,
    @SerialName("type")
    val type: Int,
    @SerialName("dynamic_id")
    val dynamicId: Long,
    @SerialName("origin")
    val origin: DynamicDescribe? = null,
    @SerialName("orig_dy_id")
    val originDynamicId: Long? = null,
    @SerialName("orig_type")
    val originType: Int? = null,
    @SerialName("timestamp")
    val timestamp: Long,
    @SerialName("user_profile")
    val profile: JsonElement? = null
){
    val dynamicTime get() = getDynamicTimeFromID(dynamicId)
}

@Serializable
data class DynamicDisplay(
    @SerialName("origin")
    val origin: DynamicDisplay? = null,
    @SerialName("emoji_info")
    val emojiInfo: EmojiInfo? = null
)

@Serializable
data class EmojiInfo(
    @SerialName("emoji_details")
    val emojiDetails: List<EmojiDetails>? = null
)

@Serializable
data class EmojiDetails(
    @SerialName("id")
    val id: Int,
    @SerialName("emoji_name")
    val emojiName: String,
    @SerialName("url")
    val url: String
)