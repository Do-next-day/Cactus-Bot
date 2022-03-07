package icu.dnddl.plugin.genshin.api.mihoyobbs.data

import icu.dnddl.plugin.genshin.api.genshin.GenshinBBSApi.GenshinServer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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