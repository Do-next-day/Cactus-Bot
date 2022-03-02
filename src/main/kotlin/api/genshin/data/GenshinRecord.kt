package org.laolittle.plugin.genshin.api.genshin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import org.laolittle.plugin.genshin.database.AvatarElement

@Serializable
data class GenshinRecord(
    // ？
    val role: String?,

    /**
     * 角色列表
     * */
    val avatars: List<GenshiAvatarInfo>,

    /**
     * 玩家状态
     * */
    val stats: PlayerStats,

    /**
     * 城市探索度
     * */
    @SerialName("city_explorations") val cityExplorations: JsonArray, // TODO: 2022/2/27 need city data

    /**
     * 世界探索度
     * */
    @SerialName("world_explorations") val worldExplorations: List<WorldInfo.WorldExploration>,

    /**
     * 家园
     * */
    val homes: List<WorldInfo.Home>
) {
    /**
     * 玩家信息
     * */
    @Serializable
    data class PlayerStats(
        /**
         * 总计活跃天数
         * */
        @SerialName("active_day_number") val activeDays: Int,

        /**
         * 获得成就数
         * */
        @SerialName("achievement_number") val totalAchievements: Int,

        /**
         * 胜率？？
         * */
        @SerialName("win_rate") val winRate: Int,

        /**
         * 风神瞳
         * */
        @SerialName("anemoculus_number") val totalAnemoculus: Int,

        /**
         * 岩神瞳
         * */
        @SerialName("geoculus_number") val totalGeoculus: Int,

        /**
         * 雷神瞳
         * */
        @SerialName("electroculus_number") val totalElectroculus: Int,

        /**
         * 获得角色数量
         * */
        @SerialName("avatar_number") val totalAvatars: Int,

        /**
         * 解锁的传送点数量
         * */
        @SerialName("way_point_number") val unlockedPoints: Int,

        /**
         * 解锁的秘境数量
         * */
        @SerialName("domain_number") val totalDomains: Int,

        /**
         * 深渊层数
         * */
        @SerialName("spiral_abyss") val spiralAbyss: String,

        /**
         * 珍贵宝箱数
         * */
        @SerialName("precious_chest_number") val totalPreciousChests: Int,

        /**
         * 华丽宝箱数
         * */
        @SerialName("luxurious_chest_number") val totalLuxuriousChests: Int,

        /**
         * 精致宝箱数
         * */
        @SerialName("exquisite_chest_number") val totalExquisiteChests: Int,

        /**
         * 普通宝箱数
         * */
        @SerialName("common_chest_number") val totalCommonChests: Int,

        /**
         * 奇馈宝箱数
         *
         * 魔力宝箱 (bushi
         * */
        @SerialName("magic_chest_number") val totalMagicChests: Int,


        )

    @Serializable
    data class GenshiAvatarInfo(
        // unknown
        val id: Long, @SerialName("image") val imageUrl: String,


        /**
         * 角色名称
         * */
        val name: String,


        /**
         * 角色元素
         * */
        val element: AvatarElement,


        /**
         * 好感度
         * */
        val fetter: Int,


        /**
         * 等级
         * */
        val level: Int,

        /**
         * 稀有度 (星级)
         * */
        val rarity: Int,


        /**
         * 命座
         * */
        @SerialName("actived_constellation_num") val constellation: Int,


        /**
         * 人物卡片图片链接
         * */
        @SerialName("card_image") val cardImageUrl: String,

        // unknown, maybe it can be used in App
        @SerialName("is_chosen") val isChosen: Boolean
    )

    object WorldInfo {
        @Serializable
        class City

        @Serializable
        data class WorldExploration(
            /**
             * 声望等级或供奉等级, 由type决定
             * @see RegionType
             * */
            val level: Int,

            /**
             * 探索度
             * */
            @SerialName("exploration_percentage") val explorationPercentage: Int,

            /**
             * 图片链接
             * */
            @SerialName("icon") val iconUrl: String,

            /**
             * 地区名称
             * */
            val name: String,

            /**
             * 地区类型
             * */
            val type: RegionType,

            /**
             * 供奉
             * */
            val offerings: List<Offering>,

            /**
             * 地区ID, 或许是按照时间顺序
             * */
            val id: Int,
        )

        @Serializable
        data class Home(
            /**
             * 信任等阶
             * */
            val level: Int,

            /**
             * 访客数量
             * */
            @SerialName("visit_num") val totalVistors: Int,

            /**
             * 洞天仙力 (舒适度)
             * */
            @SerialName("comfort_num") val comfort: Int,

            /**
             * 获得摆件数
             * */
            @SerialName("item_num") val totalGotItems: Int,

            /**
             * 名称
             * */
            val name: String,

            /**
             * 图标地址
             * */
            @SerialName("icon") val iconUrl: String,

            /**
             * 舒适等级
             * */
            @SerialName("comfort_level_name") val comfortLevel: String,

            /**
             * 舒适度图标
             * */
            @SerialName("comfort_level_icon") val comfortLevelIconUrl: String,

            )

        /**
         * 地区供奉 (非神像)
         * */
        @Serializable
        data class Offering(
            /**
             * 昵称
             * */
            val name: String,

            /**
             * 供奉等级
             * */
            val level: Int,
        )

        enum class RegionType {
            /**
             * 声望
             * */
            Reputation,

            /**
             * 供奉
             * */
            Offering,
        }
    }
}