package org.laolittle.plugin.genshin.api.bbs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.laolittle.plugin.genshin.api.genshin.GenshinBBSApi.GenshinServer

object BBSData {
    @Serializable
    data class GameRole(
        @SerialName("game_biz") private val originGameBiz: String,
        @SerialName("region") private val originRegion: String,
        @SerialName("game_uid") val gameUID: Long,
        val nickname: String,
        val level: Int,
        @SerialName("is_chosen") val isChosen: Boolean,
        @SerialName("region_name") val regionName: String,
        @SerialName("is_official") val isOfficial: Boolean
    ) {
        val region
            get() = when (originRegion) {
                "cn_gf01" -> GenshinServer.CN_GF01
                "cn_qd01" -> GenshinServer.CN_QD01
                else -> throw IllegalStateException("未知 $originRegion")
            }

        val gameBiz
            get() = when (originGameBiz) {
                "hk4e_cn" -> GameBiz.HK4E_CN
                else -> throw IllegalStateException("未知 $originGameBiz")
            }
    }


    /**
     *
     */
    enum class GameBiz {
        /**
         * 原神
         */
        HK4E_CN;

        override fun toString() = name.lowercase()
    }
}