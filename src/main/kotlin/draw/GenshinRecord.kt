package icu.dnddl.plugin.genshin.draw

import icu.dnddl.plugin.genshin.api.genshin.data.GenshinFullRecord
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
fun GenshinFullRecord.infoImage(): Image {

    return Surface.makeRasterN32Premul(bgWidth, bgHeight).apply {
        canvas.apply {
            // 背景
            recordBg.draw(this, 0, 0, Paint())
            // 左半信息卡片
            recordInfo().draw(this, 30, 0, Paint())

            minorInfo().draw(this, 815, 45, null)


        }
    }.makeImageSnapshot()
}

private val nameCardImage = getImageFromResource("$resourceFolder/UI_NameCardPic_Kokomi_P.png")
fun GenshinFullRecord.recordInfo(): Surface {
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
                    getOrDownload(record.avatars.random().imageUrl)
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
            val genshinFont = Fonts["GenshinSans-Bold", 35f]
            val whiteFont = Paint().apply { color = Color.WHITE }
            if (null != level) {
                drawLevelBox(35f, 340f, Color.makeRGB(165, 185, 130))
                drawLevelBox(330f, 340f, Color.makeRGB(205, 185, 165))

                drawTextLine(TextLine.make("冒险等阶", genshinFont), 45f, 380f, whiteFont)
                // todo
            }

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
            val uidText = TextLine.make("UID: $uid", genshinFont)
            drawTextLine(uidText, (nameCardWidth - uidText.width) / 2 + leftPadding, 80f, whiteFont)

            // 头像框
            val avatarRadius = 100f
            drawAvatarFrame(
                avatarRadius,
                (nameCardWidth / 2 + leftPadding).toFloat(),
                (nameCardHeight - avatarRadius / 4)
            )

            //drawTextLine(TextLine.make("${this@recordInfo}"))
            val fP = Paint().apply {
                color = Color.makeRGB(60, 70, 85)
            }
            averLayer(Rect(61f, 431f, 648f, 546f), 0f, 2, 1) {
                val genshinFont25 = genshinFont.makeWithSize(20f)
                box(1) {
                    val r = Rect(123f, 360f, 210f, 453f)
                    drawImageRectNearest(spriteIcon, r, Rect.makeXYWH(0f, -10f, r.width * 1.42f, r.height * 1.42f))
                    translate(r.width * 1.42f, 0f)

                    val l = TextLine.make("成就总数", genshinFont25)

                    drawTextLine(l, 10f, 40f, fP)

                    val b = TextLine.make(record.stats.totalAchievements.toString(), genshinFont)
                    drawTextLine(b, 10f, 80f, fP)
                }

                box(2) {
                    val r = Rect(17f, 360f, 110f, 453f)
                    drawImageRectNearest(spriteIcon, r, Rect.makeXYWH(0f, -10f, r.width * 1.42f, r.height * 1.42f))
                    translate(r.width * 1.42f, 0f)

                    val l = TextLine.make("深境螺旋", genshinFont25)

                    drawTextLine(l, 10f, 40f, fP)

                    val b = TextLine.make(record.stats.spiralAbyss, genshinFont)
                    drawTextLine(b, 10f, 80f, fP)
                }
            }

            averLayer(Rect(10f, 560f, 580f, 880f), 12f, 3, 3) {
                val fontT = Fonts["MiSans-Regular", 35f]
                val fontB = fontT.makeWithSize(20f)

                data class Record(
                    val src: Rect,
                    val title: String,
                    val info: String,
                )

                listOf(
                    Record(Rect.makeXYWH(213f, 86f, 60f, 60f), "活跃天数", record.stats.activeDays.toString()),
                    Record(Rect.makeXYWH(282f, 85f, 60f, 60f), "成就达成数", record.stats.totalAchievements.toString()),
                    Record(Rect.makeXYWH(353f, 81f, 60f, 60f), "获得角色数", record.stats.totalAvatars.toString()),
                    Record(Rect.makeXYWH(201f, 16f, 50f, 60f), "解锁传送点", record.stats.unlockedPoints.toString()),
                    Record(Rect.makeXYWH(10f, 9f, 60f, 60f), "解锁秘境", record.stats.totalDomains.toString()),
                    Record(Rect.makeXYWH(256f, 14f, 60f, 60f), "深境螺旋", record.stats.spiralAbyss),
                    Record(Rect.makeXYWH(17f, 76f, 55f, 60f), "风神瞳", record.stats.totalAnemoculus.toString()),
                    Record(Rect.makeXYWH(75f, 80f, 60f, 60f), "岩神瞳", record.stats.totalGeoculus.toString()),
                    Record(Rect.makeXYWH(144f, 77f, 60f, 60f), "雷神瞳", record.stats.totalElectroculus.toString())
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
                            spriteIcon,
                            record.src,
                            dst
                        )

                        translate(105f, 0f)

                        drawTextLine(topText, 0f, (boxHeight + topText.xHeight) / 2, fP)
                        drawTextLine(bottomText, 0f, (boxHeight + bottomText.height) / 2 + bottomText.height * .67f, fP)
                    }
                }
            }
        }
    }
}

fun GenshinFullRecord.minorInfo(): Surface {
    val w = 1490 - 880f
    val h = 895 - 15f
    return Surface.makeRasterN32Premul(w.toInt(), h.toInt()).apply {
        val home = getImageFromResource("/GenshinRecord/UI_HomeworldModule_3_Pic.png")

        val homeCardWidth = w
        val homeCardHeight = homeCardWidth / 2.65f

        canvas.apply {
            save()
            clipRRect(RRect.makeComplexLTRB(0f, 0f, homeCardWidth, homeCardHeight, floatArrayOf(10f)), true)
            //drawRRect(RRect.makeComplexLTRB(0f, 0f, homeCardWidth, homeCardHeight, floatArrayOf(10f)), Paint())
            drawImageRectNearest(home, Rect(0f, 0f, homeCardWidth, homeCardHeight), Paint().apply {
                //blendMode = BlendMode.SRC_ATOP
            })
            restore()
            translate(0f, 90f)
            drawRect(Rect(0f, 0f, homeCardWidth, homeCardHeight), Paint().apply {
                color = Color.BLACK
                alpha = 50
                blendMode = BlendMode.SRC_ATOP
            })

            //val space = 40f
            val font = Fonts["MiSans-Regular", 35f]
            val fontTwo = font.makeWithSize(20f)
            val fontColor = Color.makeRGB(253, 253, 253)

            val dataMap: MutableMap<String, String> = mutableMapOf()
            val homeData = record.homes.first()
            dataMap["信任等阶"] = homeData.level.toString()
            dataMap["最高洞天仙力"] = homeData.comfort.toString()
            dataMap["获得摆件数"] = homeData.totalGotItems.toString()
            dataMap["历史访客数"] = homeData.totalVisitors.toString()

//                translate(0f, 70f)
//                var offsetX = 0f
//                dataMap.forEach { (key, value) ->
//                    translate(space + offsetX, 0f)
//                    offsetX = drawTitleData(value, key, font, fontTwo, fontColor)
//                }
//                resetMatrix()
            averLayer(Rect(20f, 0f, 580f, 130f), 0f, 4, 1) {
                var i = 1
                //val font18 = font.makeWithSize(18f)
                dataMap.forEach { (key, value) ->
                    box(i) {

                        drawTitleData(value, key, font, fontTwo, Rect.makeXYWH(0f, 0f, boxWidth, boxHeight), fontColor)
                    }
                    i++
                }
            }

            resetMatrix()
            translate(0f, homeCardHeight + 30)
            translate(0f, 280f) // delete later
            drawTextLine(TextLine.make("施工中", font.makeWithSize(160f)), 40f, 0f, Paint())

            /**
             *


            var i = 0
            val boxW = 295f
            val boxH = 130f
            val pad = w - (boxW * 2)
            val zhong = getImageFromResource("UI_AvatarIcon_Zhongli.png")
            for (y in 0..1) {
            for (x in 0..1) {
            // todo draw image
            val left = (w - boxW) * x
            val top = (boxH + pad) * y
            drawRRect(
            RRect.makeComplexXYWH(
            left,
            top,
            boxW,
            boxH,
            floatArrayOf(5f)),
            Paint().apply {
            color = Color.CYAN
            }
            )

            drawImageRectNearest(zhong, Rect.makeXYWH
            (left,
            top,
            boxW,
            boxH,))
            i++
            }
            }

            translate(0f, boxH * 2 + pad + 30)
            val cBg = getImageFromResource("SpriteAtlasTexture-ui_sprite_general_quality_bg-512x256-fmt25.png")
            val cW = 120f
            val cH = cW * (95f/78)
            val rectCBg = Rect.makeXYWH(165f, 59f, 78f, 95f)
            repeat(2) { t ->
            val off = 15f + (cW + 30) * t
            val leftRect = Rect.makeXYWH(off, 0f, cW, cH)
            val rightRect = Rect(w - (off + cW), 0f, w- off, cH)
            drawImageRectNearest(cBg, rectCBg, leftRect)
            drawImageRectNearest(cBg, rectCBg, rightRect)

            // draw image
            drawImageClipHeight(zhong, leftRect)
            drawImageClipHeight(zhong, rightRect)
            }
             */
        }
    }
}

private val infoBg: Surface by lazy {
    infoBG.zoomAroundAtCornerWidth(56f, infoBgWidth, infoBgHeight).apply {
        canvas.apply {
            // 200 205 180

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

private val spriteIcon = getImageFromResource("/GenshinRecord/SpriteAtlasTextureIcon.png")
private val infoBG = getImageFromResource("$resourceFolder/UI_FriendInfo_BG.png")
private val lineNC = getImageFromResource("$resourceFolder/UI_FriendInfo_Line_NC.png")