package org.laolittle.plugin.genshin.api.genshin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import org.jetbrains.skia.*
import org.laolittle.plugin.genshin.api.genshin.data.GenshinRecord.WorldExploration.RegionType
import org.laolittle.plugin.genshin.api.genshin.data.GenshinRecord.WorldExploration.RegionType.Offering
import org.laolittle.plugin.genshin.api.genshin.data.GenshinRecord.WorldExploration.RegionType.Reputation
import org.laolittle.plugin.genshin.database.AvatarElement
import org.laolittle.plugin.getBytes
import java.io.File

/**
 * 原神玩家信息记录
 *
 * @param role 未知
 * @param avatars 角色列表
 * @param stats 玩家状态
 * @param cityExplorations 城市探索度
 * @param worldExplorations 世界探索度
 * @param homes 家园
 */
@Serializable
data class GenshinRecord(
    val role: String?,
    val avatars: List<GenshiAvatarInfo>,
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
    data class GenshiAvatarInfo(
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
     * @param offerings 供奉的对象[Offering]
     * @param id 地区ID, 或许是按照时间顺序
     */
    @Serializable
    data class WorldExploration(
        /**
         * 声望等级或供奉等级, 由type决定
         * @see RegionType
         * */
        val level: Short,
        @SerialName("exploration_percentage") val explorationPercentage: Short,
        @SerialName("icon") val iconUrl: String,
        val name: String,
        val type: RegionType,
        val offerings: List<Offering>,
        val id: Short,
    ) {
        /**
         * 地区供奉 (非神像)
         *
         * @param name 昵称
         * @param level 供奉等级
         * */
        @Serializable
        data class Offering(
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
        @SerialName("visit_num") val totalVisitors: Int,
        @SerialName("comfort_num") val comfort: Int,
        @SerialName("item_num") val totalGotItems: Int,
        val name: String,
        @SerialName("icon") val iconUrl: String,
        @SerialName("comfort_level_name") val comfortLevel: String,
        @SerialName("comfort_level_icon") val comfortLevelIconUrl: String,
    )

    val infoImage: Image get() {
        Surface.makeRasterN32Premul(1500, 950).apply {
            canvas.apply {
                drawImage(backGroundImage, 30f, 30f)

                drawInfoBgMain(30f, 20f, 600f, 830f)

                drawImageRect(
                    Image.makeFromEncoded(File("resource/UI_Codex_Scenery_DQ2shanhugong #214208.png").readBytes()),
                    Rect.makeXYWH(35f, 25f, 638f, 319f)
                )

                // (30 + 600) / 2
                drawCircle(350f, 300f, 100f, Paint().apply {
                    color = Color.makeRGB(210, 160, 120)
                })
                drawCircle(350f, 300f, 95f, Paint().apply {
                    mode = PaintMode.STROKE
                    strokeWidth = 10f
                    color = Color.makeRGB(240, 235, 227)
                })
                drawCircle(350f, 300f, 100f, Paint().apply {
                    mode = PaintMode.STROKE
                    strokeWidth = 3.5f
                    color = Color.makeRGB(220, 200, 165)
                })
            }
            File("bg.png").writeBytes(makeImageSnapshot().getBytes())
        }
        return backGroundImage
    }

    private companion object {
        val backGroundImage: Image by lazy {
            val bgl = Image.makeFromEncoded(File("resource/UI_FriendInfo_BGL.png").readBytes())
            val bgr = Image.makeFromEncoded(File("resource/UI_FriendInfo_BGR.png").readBytes())
            val bgc = Image.makeFromEncoded(File("resource/UI_FriendInfo_BGC.png").readBytes())

            Surface.makeRasterN32Premul(1485, 900).apply {
                canvas.apply {
                    // ------------------------- BackGround ---------------------------------------
                    drawImageRect(bgl, Rect(3f, 46f, 47f, 47f), Rect.makeXYWH(2f, 44f, 44f, 900 - 50f)) // 左竖直
                    drawImageRect(bgl, Rect(45f, 3f, 47f, 47f), Rect.makeXYWH(46f, 2f, 1485 - 55f, 44f)) // 上水平
                    drawImageRect(bgr, Rect(0f, 46f, 44f, 47f), Rect.makeXYWH(1485 - 48f, 44f, 46f, 900 - 50f)) // 右竖直
                    drawImageRect(bgl, Rect(45f, 46f, 47f, 91f), Rect.makeXYWH(46f, 900 - 45f, 1485 - 60f, 44f)) // 下水平

                    drawImageRect(bgl, Rect(1f, 1f, 47f, 45f), Rect.makeXYWH(0f, 0f, 46f, 44f)) // 左上角
                    drawImageRect(bgr, Rect(0f, 1f, 46f, 45f), Rect.makeXYWH(1485 - 46f, 0f, 46f, 44f)) // 右上角
                    drawImageRect(bgl, Rect(1f, 48f, 47f, 92f), Rect.makeXYWH(0f, 900 - 44f, 46f, 44f)) // 左下角
                    drawImageRect(bgr, Rect(0f, 48f, 46f, 92f), Rect.makeXYWH(1485f - 46f, 900 - 44f, 46f, 44f)) // 右下角

                    drawRect(Rect(45f, 45f, 1437f, 860f), Paint().apply {
                        color = Color.makeRGB(240, 235, 227)
                    })

                    // ----------------------------------------------------------------------------

                }
                File("bg.png").writeBytes(makeImageSnapshot().getBytes())
            }.makeImageSnapshot()
        }

        private fun Canvas.drawInfoBgMain(l: Float, t: Float, w: Float, h: Float) {
            val bgMain = Image.makeFromEncoded(File("resource/UI_FriendInfo_Bg.png").readBytes())
            drawImageRect(bgMain, Rect(2f, 8f, 55f, 55f), Rect.makeXYWH(l - 5, t, 46f, 47f)) // 左上角
            drawImageRect(bgMain, Rect(2f, 55f, 55f,97f), Rect.makeXYWH(l - 5, t + 47f, 46f, h)) // 左竖直

            drawImageRect(bgMain, Rect(9f, 115f ,55f ,169f), Rect.makeXYWH(l, t + h + 47f, 46f, 54f)) // 左下角
            drawImageRect(bgMain, Rect(56f, 115f, 85f, 169f), Rect.makeXYWH(l+ 46, t + h + 47f, w, 54f)) // 下水平

            drawImageRect(bgMain, Rect(90f, 2f, 115f, 55f), Rect.makeXYWH(l + 41, t - 6 , w, 53f)) // 上水平
            drawImageRect(bgMain, Rect(115f, 115f, 165f, 162f), Rect.makeXYWH(l + w + 1, t + h + 47f, 50f, 47f)) // 右下角
            drawImageRect(bgMain, Rect(115f, 2f, 162f, 55f), Rect.makeXYWH(l + w, t - 6f, 48f, 53f)) // 右上角
            drawImageRect(bgMain, Rect(115f, 55f, 162f, 97f), Rect.makeXYWH(l + w + 1, t + 47f, 47f, h)) // 右竖直
        }

        fun Canvas.drawInfoBgA(l: Float, t: Float, len: Float) {
            val image = Image.makeFromEncoded(File("resource/UI_FriendInfo_BgA.png").readBytes())
            drawRect(Rect.makeXYWH(l + 40, t + 2, len, 114f), Paint().apply {
                mode = PaintMode.STROKE
                strokeWidth = 2f
                color = Color.makeRGB(213, 191, 145)
            })
            drawRect(Rect.makeXYWH(l + 40, t + 3, len, 112f), Paint().apply {
                color = Color.makeRGB(240,240,235)
            })
            drawImageRect(image, Rect.makeXYWH(0f, 0f, 40f, 118f), Rect.makeXYWH(l, t, 40f, 118f))
            drawImageRect(image, Rect.makeXYWH(40f, 0f, 40f, 118f), Rect.makeXYWH(l + 40 + len, t, 40f, 118f))
        }
    }
}