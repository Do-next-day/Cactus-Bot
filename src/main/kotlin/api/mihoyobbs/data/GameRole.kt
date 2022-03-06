package org.laolittle.plugin.genshin.api.mihoyobbs.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.laolittle.plugin.genshin.api.genshin.GenshinBBSApi.GenshinServer

@Serializable
data class GameRole(
    @SerialName("game_biz") val gameBiz: GameBiz,
    val region: GenshinServer,
    @SerialName("game_uid") val gameUID: Long,
    val nickname: String,
    val level: Int,
    @SerialName("is_chosen") val isChosen: Boolean,
    @SerialName("region_name") val regionName: String,
    @SerialName("is_official") val isOfficial: Boolean
) {
    /**
     *
     */
    @Serializable
    enum class GameBiz {
        /**
         * 原神
         */
        @SerialName("hk4e_cn")
        HK4E_CN;

        override fun toString() = name.lowercase()
    }
}