package org.laolittle.plugin.genshin.api.genshin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.skia.*
import org.laolittle.plugin.Fonts
import org.laolittle.plugin.genshin.CactusBot
import org.laolittle.plugin.genshin.service.PluginDispatcher
import org.laolittle.plugin.genshin.util.getOrDownload
import kotlin.math.round

@Serializable
data class DailyNote(
    /**
     * 当前树脂
     */
    @SerialName("current_resin") val currentResin: Short,

    /**
     * 最大树脂
     */
    @SerialName("max_resin") val maxResin: Short,

    /**
     * 树脂恢复完毕时间 (单位: 秒)
     */
    @SerialName("resin_recovery_time") val resinRecoveryTime: Long,

    /**
     * 每日委托完成数量
     */
    @SerialName("finished_task_num") val finishedTask: Short,

    /**
     * 每日委托总数
     */
    @SerialName("total_task_num") val totalTask: Short,

    /**
     * 是否领取奖励
     */
    @SerialName("is_extra_task_reward_received") val rewardRecived: Boolean,

    /**
     * 周本减半剩余次数
     */
    @SerialName("remain_resin_discount_num") val resinDiscountRemain: Short,

    /**
     * 周本减半最大次数
     */
    @SerialName("resin_discount_num_limit") val resinDiscountLimit: Short,

    /**
     * 目前派遣数量
     */
    @SerialName("current_expedition_num") val currentExpedition: Short,

    /**
     * 最大派遣数量
     */
    @SerialName("max_expedition_num") val maxExpedition: Short,

    /**
     * 派遣角色具体状态
     */
    @SerialName("expeditions") val expeditions: List<AvatarExploreInfo>,

    /**
     * 家园币
     */
    @SerialName("current_home_coin") val currentHomeCoin: Int,

    /**
     * 最大家园币
     */
    @SerialName("max_home_coin") val maxHomeCoin: Int,

    /**
     * 家园币剩余恢复时间 (单位: 秒)
     */
    @SerialName("home_coin_recovery_time") val homeCoinRecoveryTime: Long,

    /**
     * 日历URL (可能为Empty)
     */
    @SerialName("calendar_url") val calendarUrl: String,
) {
    /**
     * 角色派遣状态
     */
    @Serializable
    data class AvatarExploreInfo(
        @SerialName("avatar_side_icon") val avatarIconUrl: String,
        val status: Status,
        @SerialName("remained_time") val remaining: Long
    ) {
        enum class Status {
            /**
             * 已完成
             */
            Finished,

            /**
             * 派遣中
             */
            Ongoing
        }
    }

    val image: Image
        get() {
            val w = 745
            val h = 1200
            return Surface.makeRasterN32Premul(w, h).apply {
                canvas.apply {
                    clear(Color.makeRGB(240, 235, 230))
                    val paintBox = Paint().apply {
                        color = Color.makeRGB(245, 240, 235)
                    }
                    val paintInfo = Paint().apply {
                        color = Color.makeRGB(235, 230, 215)
                    }
                    val paintBorder = Paint().apply {
                        mode = PaintMode.STROKE
                        color = Color.makeRGB(225, 220, 210)
                    }
                    val paintBlackText = Paint().apply {
                        color = Color.makeRGB(100, 100, 100)
                    }
                    val paintGrayText = Paint().apply {
                        color = Color.makeRGB(200, 185, 165)
                    }
                    val paintBrownText = Paint().apply {
                        color = Color.makeRGB(115, 80, 45)
                    }
                    val fontBig = Fonts["MiSans-Regular", 28f]
                    val fontSmall = Fonts["MiSans-Regular", 24f]
                    listOf(resin, homeCoin, dailyTask, weeklyZones).forEachIndexed { time, note ->
                        val top = 60f + 125 * time
                        val box = Rect.makeXYWH(30f, top, 685f, 105f)
                        val infoBox = Rect.makeXYWH(555f, top, 160f, 105f)
                        val title = TextLine.make(note.title, fontBig)
                        val subtitle = TextLine.make(note.subtitle, fontSmall)
                        val info = TextLine.make(note.info, fontBig)
                        drawRect(box, paintBox)
                        drawRect(infoBox, paintInfo)
                        drawImage(Back, 30f, top)
                        drawImage(note.image, 55f, top + 20)

                        drawTextLine(title, 100f, top + 50, paintBlackText)
                        drawTextLine(subtitle, 100f, top + 90, paintGrayText)
                        drawTextLine(info, 635f - info.width * .5f, top + 52.5f + info.height * .25f, paintBrownText)

                        drawRect(box, paintBorder)
                    }

                    val exp = Rect.makeXYWH(30f, 555f, 685f, 565f)
                    drawRect(exp, paintBox)
                    drawRect(exp, paintBorder)

                    val paintGreen = Paint().apply {
                        mode = PaintMode.STROKE
                        color = Color.makeRGB(125, 185, 15)
                        strokeWidth = 3f
                    }

                    val paintWhite = Paint().apply {
                        color = Color.WHITE
                    }

                    val paintGreenText = Paint().apply {
                        color = Color.makeRGB(125, 185, 15)
                    }

                    val finishedText = TextLine.make("探险完成", fontSmall)

                    drawTextLine(TextLine.make("探索派遣限制（${currentExpedition}/${maxExpedition}）", fontBig), 60f, 615f, paintBlackText)
                    expeditions.forEachIndexed { index, info ->
                        val avatar = Image.makeFromEncoded(PluginDispatcher.runBlocking {
                            getOrDownload(info.avatarIconUrl)
                        })

                        val top = 675f + 95 * index
                        val firstPoint = Point(95f, top)
                        val lastPoint = Point(660f, top)

                        drawPath(Path().apply {
                            moveTo(firstPoint)
                            lineTo(lastPoint)
                        }, Paint().apply {
                            mode = PaintMode.STROKE
                            strokeCap = PaintStrokeCap.ROUND
                            strokeWidth = 80f
                            shader = Shader.makeLinearGradient(
                                firstPoint,
                                lastPoint,
                                intArrayOf(Color.makeRGB(235, 230, 220), Color.TRANSPARENT)
                            )
                        })

                        drawCircle(120f, top, 30f, paintWhite)
                        drawCircle(120f, top, 25f, paintGreen)
                        drawImageRect(
                            avatar,
                            Rect.makeXYWH(
                                70f,
                                top - 70,
                                (avatar.width * 0.8).toFloat(),
                                (avatar.height * 0.8 - 5).toFloat()
                            )
                        )
                        drawTextLine(
                            if (info.status == AvatarExploreInfo.Status.Finished) finishedText
                            else TextLine.make(buildString {
                                append("剩余探索时间 ")
                                val hour = info.remaining / 3600
                                if (hour > 0) append("${hour}小时")
                                append(
                                    if (info.remaining < 60) "${info.remaining}秒"
                                    else {
                                        val minute = (round(info.remaining / 60f) - hour * 60).toInt()
                                        if (minute > 0)
                                            "${minute}分钟"
                                        else ""
                                    }
                                )
                            }, fontSmall),
                            170f,
                            top + 10,
                            if (info.status == AvatarExploreInfo.Status.Ongoing) paintGrayText else paintGreenText // 为了消去烦人的感叹号
                        )
                    }
                }
            }.makeImageSnapshot()
        }

    private data class Note(
        val image: Image,
        val title: String,
        val subtitle: String,
        val info: String
    )

    private val resin
        get() = Note(
            CactusBot::class.java.getResource("/DailyNote/resin.png")!!.openStream().use {
                Image.makeFromEncoded(it.readBytes())
            },
            "原粹树脂",
            when (resinRecoveryTime) {
                0L -> "树脂已满"
                else -> {
                    val hour = resinRecoveryTime / 3600
                    buildString {
                        append("将于")
                        if (hour > 0) append("${hour}小时")
                        append(
                            if (resinRecoveryTime < 60) "${resinRecoveryTime}秒"
                            else {
                                val minute = (round(resinRecoveryTime / 60f) - hour * 60).toInt()
                                if (minute > 0)
                                    "${minute}分钟"
                                else ""
                            }
                        )
                        append("后全部恢复")
                    }
                }
            },
            "$currentResin/$maxResin"
        )

    private val homeCoin
        get() = Note(
            CactusBot::class.java.getResource("/DailyNote/home_coin.png")!!.openStream().use {
                Image.makeFromEncoded(it.readBytes())
            },
            "洞天财翁 - 洞天宝钱",
            when (homeCoinRecoveryTime) {
                0L -> "已满"
                else -> {
                    val hour = homeCoinRecoveryTime / 3600
                    buildString {
                        append("预计")
                        if (hour > 0) append("${hour}小时")
                        append(
                            if (homeCoinRecoveryTime < 60) "${homeCoinRecoveryTime}秒"
                            else {
                                val minute = (round(homeCoinRecoveryTime / 60f) - hour * 60).toInt()
                                if (minute > 0)
                                    "${minute}分钟"
                                else ""
                            }
                        )
                        append("后达到存储上限")
                    }
                }
            },
            "$currentHomeCoin/$maxHomeCoin"
        )

    private val dailyTask
        get() = Note(
            CactusBot::class.java.getResource("/DailyNote/daily_task.png")!!.openStream().use {
                Image.makeFromEncoded(it.readBytes())
            },
            "每日委托任务",
            when {
                rewardRecived -> "「每日委托」奖励已领取"
                finishedTask == totalTask -> "「每日委托」奖励暂未领取"
                else -> "今日完成委托数量不足"
            },
            "$finishedTask/$totalTask"
        )

    private val weeklyZones
        get() = Note(
            CactusBot::class.java.getResource("/DailyNote/weekly_zones.png")!!.openStream().use {
                Image.makeFromEncoded(it.readBytes())
            },
            "值得铭记的强敌",
            "本周剩余消耗减半次数",
            "$resinDiscountRemain/$resinDiscountLimit"
        )

    companion object {
        val Back by lazy {
            CactusBot::class.java.getResource("/DailyNote/back.png")!!.openStream().use {
                Image.makeFromEncoded(it.readBytes())
            }
        }
    }
}