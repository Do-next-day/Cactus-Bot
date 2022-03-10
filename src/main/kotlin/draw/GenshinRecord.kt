package icu.dnddl.plugin.genshin.draw

import icu.dnddl.plugin.genshin.api.genshin.data.GenshinRecord
import icu.dnddl.plugin.genshin.service.PluginDispatcher
import icu.dnddl.plugin.genshin.util.getOrDownload
import org.jetbrains.skia.*
import org.laolittle.plugin.Fonts

private const val bgWidth = 1500
private const val bgHeight = 920
private const val infoBgWidth = 650
private const val infoBgHeight = 920
private const val resourceFolder = "/GenshinRecord"

/**
 * @author Colter23 and LaoLittle
 */
fun GenshinRecord.infoImage(): Image {

    return Surface.makeRasterN32Premul(bgWidth, bgHeight).apply {
        canvas.apply {
            // 背景
            recordBg.draw(this, 0, 0, Paint())
            // 左半信息卡片
            recordInfo().draw(this, 30, 0, Paint())

            // todo 右半信息


        }
    }.makeImageSnapshot()
}

private val nameCardImage = getImageFromResource("$resourceFolder/UI_NameCardPic_Kokomi_P.png")
fun GenshinRecord.recordInfo(): Surface {
    fun Canvas.drawAvatarFrame(radius: Float, x: Float, y: Float) {
        // (30 + 600) / 2
        // 中部实心圆
        Surface.makeRasterN32Premul((radius * 2 + 2).toInt(), (radius * 2 + 2).toInt()).apply {
            canvas.apply {
                val paint = Paint()
                drawCircle(radius + 1, radius + 1, radius, Paint().apply {
                    color = Color.makeRGB(210, 160, 120)
                })
                val avatar = Image.makeFromEncoded(PluginDispatcher.runBlocking {
                    getOrDownload(avatars.random().imageUrl)
                })

                drawImageRectNearest(avatar, Rect(0f, 0f, radius * 2, radius * 2), paint.apply {
                    blendMode = BlendMode.SRC_ATOP
                })

                // 内边框
                drawCircle(radius + 1, radius + 1, radius - 10, paint.apply {
                    blendMode = BlendMode.SRC
                    color = Color.makeRGB(220, 200, 165)
                    mode = PaintMode.STROKE
                    strokeWidth = 2f
                })
                // 中边框
                drawCircle(radius + 1, radius + 1, radius - 5, paint.apply {
                    strokeWidth = 10f
                    color = Color.makeRGB(240, 235, 227)
                })
                // 外边框
                drawCircle(radius + 1, radius + 1, radius - 1, paint.apply {
                    strokeWidth = 3.5f
                    color = Color.makeRGB(220, 200, 165)
                })
            }
        }.draw(this, (x - radius - 1).toInt(), (y - radius - 1).toInt(), null)
    }

    return Surface.makeRasterN32Premul(infoBgWidth, infoBgHeight).apply {
        canvas.apply {
            infoBg.draw(this, 0, 0, Paint())

            // 名片 比例:16/6
            val leftPadding = 15
            val topPadding = 14

            val nameCardWidth = infoBgWidth - leftPadding * 2 + 4
            val nameCardHeight = nameCardWidth * 6 / 16
            /*drawImageClipHeight(
               Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_NameCardPic_Kokomi_P.png").readBytes()),
               Rect.makeXYWH(leftPadding.toFloat() - 1, topPadding.toFloat(), nameCardWidth.toFloat() + 2, nameCardHeight.toFloat())
            )*/
            drawImageClipHeight(
                nameCardImage,
                nameCardWidth,
                nameCardHeight,
                ImagePosition.CENTER,
                Point(leftPadding.toFloat(), topPadding.toFloat())
            )

            // 透明名片装饰线
            val lineNCSf = lineNC.zoomTopAtPoint(47f, 48f, nameCardWidth + 2, nameCardHeight)
            lineNCSf.draw(this, leftPadding - 1, topPadding, Paint().setAlphaf(0.2f))

            // 头像框
            val avatarRadius = 100f
            drawAvatarFrame(
                avatarRadius,
                (nameCardWidth / 2 + leftPadding).toFloat(),
                (nameCardHeight - avatarRadius / 4)
            )
            val genshinFont = Fonts["GenshinSans-Bold", 35f]
            val whiteFont = Paint().apply { color = Color.WHITE }
            drawTextLine(TextLine.make("冒险等阶", genshinFont), 45f, 380f, whiteFont)
            //drawTextLine(TextLine.make("${this@recordInfo}"))


            val spImg = getImageFromResource("/GenshinRecord/SpriteAtlasTextureIcon.png")
            averLayer(Rect(10f, 560f, 580f, 880f), 12f, 3, 3) {
                val fontT = Fonts["MiSans-Regular", 35f]
                val fontB = fontT.makeWithSize(20f)
                val fP = Paint().apply {
                    color = Color.makeRGB(60, 70, 85)
                }

                listOf(
                    Record(Rect.makeXYWH(213f, 86f, 55f, 55f), "活跃天数", stats.activeDays.toString()),
                    Record(Rect.makeXYWH(282f, 85f, 55f, 55f), "成就达成数", stats.totalAchievements.toString()),
                    Record(Rect.makeXYWH(353f, 81f, 55f, 55f), "获得角色数", stats.totalAvatars.toString()),
                    Record(Rect.makeXYWH(201f, 16f, 55f, 55f), "解锁传送点", stats.unlockedPoints.toString()),
                    Record(Rect.makeXYWH(10f, 9f, 55f, 55f), "解锁秘境", stats.totalDomains.toString()),
                    Record(Rect.makeXYWH(256f, 14f, 55f, 55f), "深境螺旋", stats.spiralAbyss),
                    Record(Rect.makeXYWH(17f, 76f, 55f, 55f), "风神瞳", stats.totalAnemoculus.toString()),
                    Record(Rect.makeXYWH(75f, 80f, 55f, 55f), "岩神瞳", stats.totalGeoculus.toString()),
                    Record(Rect.makeXYWH(144f, 77f, 55f, 55f), "雷神瞳", stats.totalElectroculus.toString())
                ).forEachIndexed { index, record ->
                    box(index + 1) {
                        val topText = TextLine.make(record.info, fontT)
                        val bottomText = TextLine.make(record.title, fontB)

                        val dst = Rect.makeXYWH(
                            40f,
                            (boxHeight - record.src.height) / 2 + 5,
                            record.src.width,
                            record.src.height
                        )
                        drawImageRect(
                            spImg,
                            record.src,
                            dst
                        )

                        drawTextLine(topText, 45f + dst.width, (boxHeight + topText.xHeight) /2, fP)
                        drawTextLine(bottomText, 45f + dst.width, (boxHeight + bottomText.height) / 2 + bottomText.height * .67f, fP)
                    }
                }
            }
        }
    }
}

private val infoBg: Surface by lazy {
    infoBG.zoomAroundAtCornerWidth(56f, infoBgWidth, infoBgHeight).apply {
        canvas.apply {
            // 200 205 180
            drawLevelBox(35f, 340f, Color.makeRGB(165, 185, 130))
            drawLevelBox(330f, 340f, Color.makeRGB(205, 185, 165))

//                drawInfoBgA(30f, 430f, 210f)
//                drawInfoBgA(330f, 430f, 210f)
            // shiny, kira !
            val kira = getImageFromResource("$resourceFolder/UI_FriendInfo_Kira2.png")

            drawImageRect(
                kira,
                Rect(
                    infoBgWidth / 2f - 9,
                    infoBgHeight / 2f + 20,
                    infoBgWidth / 2f + 9,
                    infoBgHeight / 2f + 37
                ), Paint().apply {
                    colorFilter = ColorFilter.makeBlend(Color.makeRGB(220, 215, 205), BlendMode.SRC_ATOP)
                }
            )

            val infoBgA = getImageFromResource("$resourceFolder/UI_FriendInfo_BGA.png")
            val infoBg = infoBgA.zoomHorizontalAtPoint(39f, 40f, 290)
            infoBg.draw(this, 30, 430, null)
            infoBg.draw(this, 330, 430, null)
        }
    }
}

private val recordBg: Surface by lazy {
    Surface.makeRasterN32Premul(bgWidth, bgHeight).apply {
        canvas.apply {
            val centerWidth = 100
            val halfWidth = (bgWidth - centerWidth) / 2
            val bgPadding = Rect(0f, 10f, 0f, 10f)
            val bglSf = getImageFromResource("$resourceFolder/UI_FriendInfo_BGL.png").zoomLeftAtPoint(
                46f,
                48f,
                halfWidth,
                bgHeight,
                bgPadding
            )
            val bgrSf = getImageFromResource("$resourceFolder/UI_FriendInfo_BGR.png").zoomRightAtPoint(
                46f,
                48f,
                halfWidth,
                bgHeight,
                bgPadding
            )
            val bgcSf = getImageFromResource("$resourceFolder/UI_FriendInfo_BGC.png").zoomVerticalAtPoint(
                20f,
                36f,
                bgHeight,
                Rect(0f, 12f, 0f, 12f)
            )

            bglSf.draw(this, 0, 0, Paint())
            bgcSf.draw(this, halfWidth, 0, Paint())
            bgrSf.draw(this, halfWidth + centerWidth, 0, Paint())
        }
    }
}

private data class Record(
    val src: Rect,
    val title: String,
    val info: String,
)

private fun Canvas.drawLevelBox(l: Float, t: Float, colorR: Int) {
    drawRRect(RRect.makeComplexXYWH(l, t, 285f, 52f, floatArrayOf(1f)), Paint().apply {
        color = colorR
    })

    drawRRect(RRect.makeComplexXYWH(l, t, 285f, 52f, floatArrayOf(1f)), Paint().apply {
        mode = PaintMode.STROKE
        strokeWidth = 1.25f
        color = Color.makeRGB(200, 205, 180)
    })
}

private val infoBG = getImageFromResource("$resourceFolder/UI_FriendInfo_BG.png")
private val lineNC = getImageFromResource("$resourceFolder/UI_FriendInfo_Line_NC.png")