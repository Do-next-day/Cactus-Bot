package icu.dnddl.plugin.genshin.api.genshin.data

import icu.dnddl.plugin.genshin.api.genshin.data.GenshinRecord.WorldExploration.OfferingInfo
import icu.dnddl.plugin.genshin.api.genshin.data.GenshinRecord.WorldExploration.RegionType
import icu.dnddl.plugin.genshin.api.genshin.data.GenshinRecord.WorldExploration.RegionType.Offering
import icu.dnddl.plugin.genshin.api.genshin.data.GenshinRecord.WorldExploration.RegionType.Reputation
import icu.dnddl.plugin.genshin.database.AvatarElement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

/**
 * 原神玩家信息记录
 *
 * @param role 未知
 * @param avatars 角色列表
 * @param stats 玩家状态
 * @param cityExplorations 城市探索度
 * @param worldExplorations 世界探索度
 * @param homes 家园
 *
 * @author LaoLittle
 */
@Serializable
data class GenshinRecord(
    val role: String?,
    val avatars: List<GenshinAvatarInfo>,
    val stats: PlayerStats,
    @SerialName("city_explorations") val cityExplorations: JsonArray, // TODO: 2022/2/27 need city data
    @SerialName("world_explorations") val worldExplorations: List<WorldExploration>,
    val homes: List<Home>
) {
    /**
     * 玩家状态
     *
     * @param activeDays 总计活跃天数
     * @param totalAchievements 获得成就数
     * @param winRate 未知
     * @param totalAnemoculus 风神瞳
     * @param totalGeoculus 岩神瞳
     * @param totalElectroculus 雷神瞳
     * @param totalAvatars 获得角色数量
     * @param unlockedPoints 解锁的传送点数量
     * @param totalDomains 解锁的秘境数量
     * @param spiralAbyss 深渊层数
     * @param totalPreciousChests 珍贵宝箱数
     * @param totalLuxuriousChests 华丽宝箱数
     * @param totalExquisiteChests 精致宝箱数
     * @param totalCommonChests 普通宝箱数
     * @param totalMagicChests 奇馈宝箱数
     * */
    @Serializable
    data class PlayerStats(
        @SerialName("active_day_number") val activeDays: Int,
        @SerialName("achievement_number") val totalAchievements: Int,
        @SerialName("win_rate") val winRate: Int,
        @SerialName("anemoculus_number") val totalAnemoculus: Short,
        @SerialName("geoculus_number") val totalGeoculus: Short,
        @SerialName("electroculus_number") val totalElectroculus: Short,
        @SerialName("avatar_number") val totalAvatars: Short,
        @SerialName("way_point_number") val unlockedPoints: Short,
        @SerialName("domain_number") val totalDomains: Short,
        @SerialName("spiral_abyss") val spiralAbyss: String,
        @SerialName("precious_chest_number") val totalPreciousChests: Int,
        @SerialName("luxurious_chest_number") val totalLuxuriousChests: Int,
        @SerialName("exquisite_chest_number") val totalExquisiteChests: Int,
        @SerialName("common_chest_number") val totalCommonChests: Int,
        @SerialName("magic_chest_number") val totalMagicChests: Int,
    )

    /**
     * 角色信息
     * @param id unknown
     * @param imageUrl 角色头像图片链接
     * @param name 角色名称
     * @param element 元素
     * @param fetter 好感度
     * @param level 等级
     * @param rarity 稀有度 (星级)
     * @param constellation 命座
     * @param cardImageUrl 人物卡片图片链接
     * @param isChosen
     */
    @Serializable
    data class GenshinAvatarInfo(
        val id: Long,
        @SerialName("image") val imageUrl: String,
        val name: String,
        val element: AvatarElement,
        val fetter: Short,
        val level: Short,
        val rarity: Short,
        @SerialName("actived_constellation_num") val constellation: Short,
        @SerialName("card_image") val cardImageUrl: String,
        // unknown, maybe it can be used in App
        @SerialName("is_chosen") val isChosen: Boolean
    )

    @Suppress("unused")
    @Serializable
    class City

    /**
     * @param level
     * @param explorationPercentage 探索度
     * @param iconUrl 图片链接
     * @param name 地区名称
     * @param type 地区类型[RegionType]
     * @param offerings 供奉的对象[OfferingInfo]
     * @param id 地区ID, 或许是按照时间顺序
     */
    @Serializable
    data class WorldExploration(
        /**
         * 声望等级或供奉等级, 由type决定
         * @see RegionType
         * */
        val level: Short,
        @SerialName("exploration_percentage")
        val explorationPercentage: Short,
        @SerialName("icon")
        val iconUrl: String,
        val name: String,
        val type: RegionType,
        val offerings: List<OfferingInfo>,
        val id: Short,
    ) {
        /**
         * 地区供奉 (非神像)
         *
         * @param name 昵称
         * @param level 供奉等级
         * */
        @Serializable
        data class OfferingInfo(
            val name: String,
            val level: Short,
        )

        /**
         * [Reputation]
         *
         * [Offering]
         */
        enum class RegionType {
            /**
             * 声望
             */
            Reputation,

            /**
             * 供奉
             * */
            Offering,
        }
    }

    /**
     * @param level 信任等阶
     * @param totalVisitors 访客数量
     * @param comfort 洞天仙力 (舒适度)
     * @param totalGotItems 获得摆件数
     * @param name 名称
     * @param iconUrl 图标地址
     * @param comfortLevel 舒适等级
     * @param comfortLevelIconUrl 舒适度图标
     */
    @Serializable
    data class Home(
        val level: Short,
        @SerialName("visit_num")
        val totalVisitors: Int,
        @SerialName("comfort_num")
        val comfort: Int,
        @SerialName("item_num")
        val totalGotItems: Int,
        val name: String,
        @SerialName("icon")
        val iconUrl: String,
        @SerialName("comfort_level_name")
        val comfortLevel: String,
        @SerialName("comfort_level_icon")
        val comfortLevelIconUrl: String,
    )
}