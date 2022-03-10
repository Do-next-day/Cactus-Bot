package icu.dnddl.plugin.genshin.draw

import icu.dnddl.plugin.genshin.api.genshin.data.DailyNote
import icu.dnddl.plugin.genshin.api.genshin.data.DailyNote.AvatarExploreInfo.Status.Finished
import icu.dnddl.plugin.genshin.api.genshin.data.DailyNote.AvatarExploreInfo.Status.Ongoing
import icu.dnddl.plugin.genshin.service.PluginDispatcher
import icu.dnddl.plugin.genshin.util.getOrDownload
import org.jetbrains.skia.*
import org.laolittle.plugin.Fonts
import kotlin.math.round

/**
 * @author LaoLittle
 */
fun DailyNote.infoImage(): Image {
    val w = 745
    val h = 1200

    val homeCoin = Note(
        homeCoinImage,
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


    val dailyTask = Note(
        dailyTaskImage,
        "每日委托任务",
        when {
            rewardReceived -> "「每日委托」奖励已领取"
            finishedTask == totalTask -> "「每日委托」奖励暂未领取"
            else -> "今日完成委托数量不足"
        },
        "$finishedTask/$totalTask"
    )

    val resin = Note(
        resinImage,
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

    val weeklyZones = Note(
        weeklyZonesImage,
        "值得铭记的强敌",
        "本周剩余消耗减半次数",
        "$resinDiscountRemain/$resinDiscountLimit"
    )

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
                drawImage(back, 30f, top)
                drawImage(note.image, 55f, top + 20)

                drawTextLine(title, 100f, top + 50, paintBlackText)
                drawTextLine(subtitle, 100f, top + 90, paintGrayText)
                drawTextLine(info, 635f - info.width * .5f, top + 52.5f + info.height * .25f, paintBrownText)

                drawRect(box, paintBorder)
            }

            val exp = Rect.makeXYWH(30f, 555f, 685f, 565f)
            drawRect(exp, paintBox)
            drawRect(exp, paintBorder)



            val paintWhite = Paint().apply {
                color = Color.WHITE
            }

            val paintGreenText = Paint().apply {
                color = Color.makeRGB(125, 185, 15)
            }

            val finishedText = TextLine.make("探险完成", fontSmall)

            drawTextLine(
                TextLine.make("探索派遣限制（${currentExpedition}/${maxExpedition}）", fontBig),
                60f,
                615f,
                paintBlackText
            )
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

                val paintCircleBorder = Paint().apply {
                    mode = PaintMode.STROKE
                    color = when(info.status) {
                        Finished -> Color.makeRGB(125, 185, 15)
                        Ongoing -> Color.makeRGB(220,155,75)
                    }
                    strokeWidth = 3f
                }

                drawCircle(120f, top, 30f, paintWhite)
                drawCircle(120f, top, 25f, paintCircleBorder)
                drawImageRectNearest(
                    avatar,
                    Rect.makeXYWH(
                        70f,
                        top - 70,
                        (avatar.width * 0.8).toFloat(),
                        (avatar.height * 0.8 - 5).toFloat()
                    )
                )

                drawTextLine(
                    when (info.status) {
                        Finished -> finishedText
                        Ongoing -> {
                            TextLine.make(buildString {
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
                            }, fontSmall)
                        }
                    },
                    170f,
                    top + 10,
                    when (info.status) {
                        Finished -> paintGreenText
                        Ongoing -> paintGrayText
                    }
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

private val resinImage = getImageFromResource("/DailyNote/resin.png")
private val homeCoinImage = getImageFromResource("/DailyNote/home_coin.png")
private val dailyTaskImage = getImageFromResource("/DailyNote/daily_task.png")
private val weeklyZonesImage = getImageFromResource("/DailyNote/weekly_zones.png")
private val back = getImageFromResource("/DailyNote/back.png")