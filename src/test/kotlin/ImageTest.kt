@file: Suppress("unused", "UNUSED_PARAMETER")

import icu.dnddl.plugin.genshin.draw.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.jetbrains.skia.*
import org.junit.Test
import org.laolittle.plugin.getBytes
import java.io.File
import kotlin.math.max
import kotlin.system.measureTimeMillis

internal class ImageTest {
    @Test
    fun testBg(): Unit = runBlocking {

        val bgWidth = 1430
        val bgHeight = 920
        val infoBgWidth = 650
        val infoBgHeight = 920


        val infoBgMain =
            Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_Bg.png").readBytes())
        val infoBgMainSf = infoBgMain.zoomAroundAtCornerWidth(56f, infoBgWidth, infoBgHeight)
        infoBgMainSf.apply {
            canvas.apply {

                // 名片 比例:16/6
                val leftPadding = 15
                val topPadding = 14

                val nameCardWidth = infoBgWidth - leftPadding * 2 + 4
                val nameCardHeight = nameCardWidth * 6 / 16
                println(nameCardHeight)
                val nc =
                    Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_NameCardPic_Kokomi_P.png").readBytes())
//                drawImageClipHeight(
//                    Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_NameCardPic_Kokomi_P.png").readBytes()),
//                    Rect.makeXYWH(leftPadding.toFloat() - 1, topPadding.toFloat(), nameCardWidth.toFloat() + 2, nameCardHeight.toFloat())
//                )
                drawImageClipHeight(
                    nc,
                    nameCardWidth,
                    nameCardHeight,
                    ImagePosition.CENTER,
                    Point(leftPadding.toFloat(), topPadding.toFloat())
                )

                // 透明名片装饰线
                val lineNC =
                    Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_Line_NC.png").readBytes())
                val lineNCSf = lineNC.zoomTopAtPoint(47f, 48f, nameCardWidth + 2, nameCardHeight)
                lineNCSf.draw(this, leftPadding - 1, topPadding, Paint().setAlphaf(0.2f))

                // 头像框
                val avatarRadius = 100f
                drawAvatarFrame(
                    avatarRadius,
                    (nameCardWidth / 2 + leftPadding).toFloat(),
                    (nameCardHeight - avatarRadius / 4)
                )

                // 200 205 180
                drawLevelBox(35f, 340f, Color.makeRGB(165, 185, 130))
                drawLevelBox(330f, 340f, Color.makeRGB(205, 185, 165))

//                drawInfoBgA(30f, 430f, 210f)
//                drawInfoBgA(330f, 430f, 210f)
                // shiny, kira !
                val kira =
                    Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_Kira2.png").readBytes())

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

                val infoBgA =
                    Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_BgA.png").readBytes())
                val infoBg = infoBgA.zoomHorizontalAtPoint(39f, 40f, 290)
                infoBg.draw(this, 30, 430, null)
                infoBg.draw(this, 330, 430, null)


                val font = Font(Typeface.makeFromName("Noto Sans SC", FontStyle.BOLD), 35f)
                val fontTwo = font.makeWithSize(20f)
                val fontColor = Color.BLACK
                val sprite = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/SpriteAtlasTextureIcon.png").readBytes())
                val icon1 = sprite.readImageRectContent(Rect(10f, 9f, 65f, 66f))

                val dataMap: MutableMap<String, String> = mutableMapOf()
                dataMap["活跃天数"] = "537"
                dataMap["达成成就数"] = "519"
                dataMap["获得角色数"] = "39"
                dataMap["解锁传送点"] = "169"
//                drawRect(Rect(30f, 560f,600f,880f), Paint())
                averLayer(Rect(10f, 560f,580f,880f), 12f, 3, 3) {
                    var i = 1
                    dataMap.forEach { (key, value) ->
                        box(i) {
                            drawTitleData(icon1, value, key, font, fontTwo,Rect.makeXYWH(0f,0f,boxWidth,boxHeight), fontColor)
                        }
                        i++
                    }

                }
//                val p1 = Point(infoBgWidth / 2f, infoBgHeight /2f + 20)
//                val p2 = Point(infoBgWidth / 2f, infoBgHeight /2f + 36)
//                val p3 = Point(infoBgWidth / 2f - 6, infoBgHeight/ 2f + 28)
//                val p4 = Point(infoBgWidth / 2f + 6, infoBgHeight/ 2f + 28)
//
//                drawPath(Path().apply {
//                    moveTo(p1)
//                    lineTo(p3)
//                    lineTo(p2)
//                    lineTo(p4)
//                }, Paint().apply {
//                    color= Color.makeRGB(220, 215, 205)
//                }) // 小菱形
            }

            File("infoBg.png").writeBytes(makeImageSnapshot().getBytes())
        }

        // x 775 - 1490
        // y 15 - 895
        val w = 1490 - 880f
        val h = 895 - 15f
        val infoBgMinor = Surface.makeRasterN32Premul(w.toInt(), h.toInt()).apply {
            val home = getTestImage("UI_HomeworldModule_3_Pic.png")

            val homeCardWidth = w
            val homeCardHeight = homeCardWidth / 2.65f

            canvas.apply {
                drawRRect(RRect.makeComplexLTRB(0f, 0f, homeCardWidth, homeCardHeight, floatArrayOf(10f)), Paint())
                drawImageRectNearest(home, Rect(0f, 0f, homeCardWidth, homeCardHeight), Paint().apply {
                    blendMode = BlendMode.SRC_ATOP
                })
                translate(0f, 90f)
                drawRect(Rect(0f, 0f, homeCardWidth, homeCardHeight), Paint().apply {
                    color = Color.BLACK
                    alpha = 50
                    blendMode = BlendMode.SRC_ATOP
                })

                val space = 40f
                val font = Font(Typeface.makeFromName("Noto Sans SC", FontStyle.NORMAL), 35f)
                val fontTwo = font.makeWithSize(20f)
                val fontColor = Color.makeRGB(253, 253, 253)

                val dataMap: MutableMap<String, String> = mutableMapOf()
                dataMap["信任等阶"] = "10"
                dataMap["最高洞天仙力"] = "21740"
                dataMap["获得摆件数"] = "2366"
                dataMap["历史访客数"] = "13"

//                translate(0f, 70f)
//                var offsetX = 0f
//                dataMap.forEach { (key, value) ->
//                    translate(space + offsetX, 0f)
//                    offsetX = drawTitleData(value, key, font, fontTwo, fontColor)
//                }
//                resetMatrix()
                averLayer(Rect(20f, 0f, 580f, 130f),0f, 4,1){
                    var i = 1
                    dataMap.forEach { (key, value) ->
                        box(i) {
                            drawTitleData(value, key, font, fontTwo,Rect.makeXYWH(0f,0f,boxWidth,boxHeight), fontColor)
                        }
                        i++
                    }
                }


                resetMatrix()
                translate(0f, homeCardHeight + 30)

                var i = 0
                val boxW = 295f
                val boxH = 130f
                val pad = w - (boxW * 2)
                val zhong = getTestImage("UI_AvatarIcon_Zhongli.png")
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
                val cBg = getTestImage("SpriteAtlasTexture-ui_sprite_general_quality_bg-512x256-fmt25.png")
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
            }

            File("minor.png").writeBytes(makeImageSnapshot().getBytes())
        }

        val recordBGL =
            Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_BGL.png").readBytes())
        val recordBGR =
            Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_BGR.png").readBytes())
        val recordBGC =
            Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_BGC.png").readBytes())

        val bg = Surface.makeRasterN32Premul(bgWidth, bgHeight).apply {
            canvas.apply {
                val centerWidth = 100
                val halfWidth = (bgWidth - centerWidth) / 2
                val bgPadding = Rect(0f, 10f, 0f, 10f)
                val bglSf = recordBGL.zoomLeftAtPoint(46f, 48f, halfWidth, bgHeight, bgPadding)
                val bgrSf = recordBGR.zoomRightAtPoint(46f, 48f, halfWidth, bgHeight, bgPadding)
                val bgcSf = recordBGC.zoomVerticalAtPoint(20f, 36f, bgHeight, Rect(0f, 12f, 0f, 12f))

                bglSf.draw(this, 0, 0, null)
                bgcSf.draw(this, halfWidth, 0, null)
                bgrSf.draw(this, halfWidth + centerWidth, 0, null)

                infoBgMainSf.draw(this, 30, 0, null)
                infoBgMinor.draw(this, 765, 45, null)
            }
        }

        File("infoCard.png").writeBytes(bg.makeImageSnapshot().getBytes())

    }

    fun Canvas.drawTitleData(
        icon: Image,
        rowOne: String,
        rowTwo: String,
        rowOneFont: Font,
        rowTwoFont: Font,
        box: Rect,
        dataColor: Int
    ){
        drawImage(icon, 40f,(box.height-icon.height)/2 + 5,null)
        val count = save()
        translate(icon.width.toFloat(), 0f)
        drawTitleData(rowOne, rowTwo, rowOneFont, rowTwoFont, box, dataColor)
        restoreToCount(count)
    }

    fun Canvas.drawTitleData(
        rowOne: String,
        rowTwo: String,
        rowOneFont: Font,
        rowTwoFont: Font,
        box: Rect,
        dataColor: Int
    ){
        val textTop = TextLine.make(rowOne, rowOneFont)
        val textBottom = TextLine.make(rowTwo, rowTwoFont)

        val halfBoxWidth = box.width / 2
        val offsetY = (box.height - textTop.xHeight - textBottom.xHeight) / 2 + textTop.xHeight

        drawTextLine(
            textTop,
            halfBoxWidth - textTop.width / 2,
            offsetY,
            Paint().apply {
                color = dataColor
            }
        )
        drawTextLine(
            textBottom,
            halfBoxWidth - textBottom.width / 2,
            offsetY + textTop.xHeight + textBottom.xHeight,
            Paint().apply {
                color = dataColor
                alpha = 160
            }
        )
    }

    fun Image.readImageRectContent(rect: Rect): Image{
        return Surface.makeRasterN32Premul(rect.width.toInt(), rect.height.toInt()).apply {
            canvas.apply {
                drawImageRect(this@readImageRectContent, rect, Rect(0f, 0f, rect.width, rect.height))
            }
        }.makeImageSnapshot()
    }



    fun Canvas.drawBackGround() {
        val bgl = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_BGL.png").readBytes())
        val bgr = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_BGR.png").readBytes())
        val bgc =
            Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_BGC.png").readBytes()) // 100 x 56

        drawImageRect(bgl, Rect(3f, 46f, 47f, 47f), Rect.makeXYWH(12f, 54f, 44f, 900 - 50f)) // 左竖直
        drawImageRect(bgl, Rect(45f, 3f, 47f, 47f), Rect.makeXYWH(56f, 12f, 1485 - 55f, 44f)) // 上水平
        drawImageRect(bgr, Rect(0f, 46f, 44f, 47f), Rect.makeXYWH(1485 - 38f, 54f, 46f, 900 - 50f)) // 右竖直
        drawImageRect(bgl, Rect(45f, 46f, 47f, 91f), Rect.makeXYWH(56f, 900 - 35f, 1485 - 60f, 44f)) // 下水平

        drawImageRect(bgl, Rect(1f, 1f, 47f, 45f), Rect.makeXYWH(10f, 10f, 46f, 44f)) // 左上角
        drawImageRect(bgr, Rect(0f, 1f, 46f, 45f), Rect.makeXYWH(1495 - 46f, 10f, 46f, 44f)) // 右上角
        drawImageRect(bgl, Rect(1f, 48f, 47f, 92f), Rect.makeXYWH(10f, 900 - 34f, 46f, 44f)) // 左下角
        drawImageRect(bgr, Rect(0f, 48f, 46f, 92f), Rect.makeXYWH(1495f - 46f, 900 - 34f, 46f, 44f)) // 右下角

        drawImageRect(bgc, Rect(0f, 1f, 100f, 28f), Rect.makeXYWH(1505 * .5f, 12f, 100f, 27f))
        drawImageRect(bgc, Rect(0f, 27f, 100f, 54f), Rect.makeXYWH(1505 * .5f, 881f, 100f, 27f))

        drawRect(Rect(55f, 55f, 1447f, 870f), Paint().apply {
            color = Color.makeRGB(240, 235, 227)
        }) // 背景色填充

        drawImageRect(bgc, Rect(0f, 16f, 100f, 39f), Rect.makeXYWH(1505 * .5f, 39f, 100f, 850f))

    }

    fun Canvas.drawInfoBgMain(l: Float, t: Float, w: Float, h: Float) {
        val bgMain = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_Bg.png").readBytes())
        drawImageRect(bgMain, Rect(2f, 8f, 55f, 55f), Rect.makeXYWH(l - 5, t, 46f, 47f)) // 左上角
        drawImageRect(bgMain, Rect(2f, 55f, 55f, 97f), Rect.makeXYWH(l - 5, t + 47f, 46f, h)) // 左竖直

        drawImageRect(bgMain, Rect(9f, 115f, 55f, 169f), Rect.makeXYWH(l, t + h + 47f, 46f, 54f)) // 左下角
        drawImageRect(bgMain, Rect(56f, 115f, 85f, 169f), Rect.makeXYWH(l + 46, t + h + 47f, w, 54f)) // 下水平

        drawImageRect(bgMain, Rect(90f, 2f, 115f, 55f), Rect.makeXYWH(l + 41, t - 6, w, 53f)) // 上水平
        drawImageRect(bgMain, Rect(115f, 115f, 165f, 162f), Rect.makeXYWH(l + w + 1, t + h + 47f, 50f, 47f)) // 右下角
        drawImageRect(bgMain, Rect(115f, 2f, 162f, 55f), Rect.makeXYWH(l + w, t - 6f, 48f, 53f)) // 右上角
        drawImageRect(bgMain, Rect(115f, 55f, 162f, 97f), Rect.makeXYWH(l + w + 1, t + 47f, 47f, h)) // 右竖直
    }

    fun Canvas.drawLineNC() {

        val lineNC =
            Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_Line_NC.png").readBytes())
        drawImageRect(
            lineNC,
            Rect(0f, 0f, 48f, 48f),
            Rect.makeXYWH(55f, 12f, 48f, 48f),
            Paint().setAlphaf(0.2f)
        ) // 左上角
        drawImageRect(
            lineNC,
            Rect(48f, 0f, 96f, 48f),
            Rect.makeXYWH(645f, 12f, 48f, 48f),
            Paint().setAlphaf(0.2f)
        ) // 右上角

        drawImageRect(
            lineNC,
            Rect(47f, 0f, 49f, 48f),
            Rect.makeXYWH(103f, 12f, 542f, 48f),
            Paint().setAlphaf(0.2f)
        ) // 上水平
        drawImageRect(
            lineNC,
            Rect(0f, 47f, 48f, 48f),
            Rect.makeXYWH(55f, 60f, 48f, 271f),
            Paint().setAlphaf(0.2f)
        ) // 左竖直
        drawImageRect(
            lineNC,
            Rect(48f, 47f, 96f, 48f),
            Rect.makeXYWH(645f, 60f, 48f, 271f),
            Paint().setAlphaf(0.2f)
        ) // 右竖直
    }

    fun Canvas.drawAvatarFrame(radius: Float, x: Float, y: Float) {
        // (30 + 600) / 2
        // 中部实心圆
        Surface.makeRasterN32Premul((radius * 2 + 2).toInt(), (radius * 2 + 2).toInt()).apply {
            canvas.apply {
                drawCircle(radius + 1, radius + 1, radius, Paint().apply {
                    color = Color.makeRGB(210, 160, 120)
                })
                val avatar = getTestImage("UI_AvatarIcon_Zhongli.png")

                drawImageRectNearest(avatar, Rect(0f, 0f, radius * 2, radius * 2), Paint().apply {
                    blendMode = BlendMode.SRC_ATOP
                })

                // 内边框
                drawCircle(radius + 1, radius + 1, radius - 10, Paint().apply {
                    blendMode = BlendMode.SRC
                    color = Color.makeRGB(220, 200, 165)
                    mode = PaintMode.STROKE
                    strokeWidth = 2f
                })
                // 中边框
                drawCircle(radius + 1, radius + 1, radius - 5, Paint().apply {
                    mode = PaintMode.STROKE
                    strokeWidth = 10f
                    color = Color.makeRGB(240, 235, 227)
                })
                // 外边框
                drawCircle(radius + 1, radius + 1, radius - 1, Paint().apply {
                    mode = PaintMode.STROKE
                    strokeWidth = 3.5f
                    color = Color.makeRGB(220, 200, 165)
                })
            }
        }.draw(this, (x - radius - 1).toInt(), (y - radius - 1).toInt(), null)
    }

    fun Canvas.drawLevelBox(l: Float, t: Float, colorR: Int) {
        drawRRect(RRect.makeComplexXYWH(l, t, 285f, 52f, floatArrayOf(1f)), Paint().apply {
            color = colorR
        })

        drawRRect(RRect.makeComplexXYWH(l, t, 285f, 52f, floatArrayOf(1f)), Paint().apply {
            mode = PaintMode.STROKE
            strokeWidth = 1.25f
            color = Color.makeRGB(200, 205, 180)
        })
    }

    fun Canvas.drawInfoBgA(l: Float, t: Float, len: Float) {
        val image = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_BgA.png").readBytes())
        drawRect(Rect.makeXYWH(l + 40, t + 2, len, 114f), Paint().apply {
            mode = PaintMode.STROKE
            strokeWidth = 2f
            color = Color.makeRGB(215, 190, 145)
        })
        drawRect(Rect.makeXYWH(l + 40, t + 3, len, 112f), Paint().apply {
            color = Color.makeRGB(240, 240, 235)
        })
        drawImageRectTo(image, Rect.makeXYWH(0f, 0f, 40f, 118f), l, t)
        drawImageRectTo(image, Rect.makeXYWH(40f, 0f, 40f, 118f), l + 40 + len, t)
    }

    @Test
    fun scaleImage() {
        val image = Image.makeFromEncoded(
            this::class.java.getResource("/UI_HomeworldModule_3_Pic.png")!!.openStream().readBytes()
        )

        Surface.makeRasterN32Premul(512, 256).apply {
            canvas.apply {
                drawImageRect(
                    image,
                    Rect(0f, 0f, image.width.toFloat(), image.height.toFloat()),
                    Rect(0f, 0f, 512f, 256f),
                    FilterMipmap(FilterMode.LINEAR, MipmapMode.NEAREST),
                    Paint().apply {
                        isAntiAlias = true
                    },
                    false
                )
            }

            File("scale_test.png").writeBytes(makeImageSnapshot().getBytes())
        }
    }

    @Test
    fun test(): Unit = runBlocking {
        println(measureTimeMillis {

            // val awtImage = ImageIO.read(File("resource/UI_ItemIcon_107001.png")).getScaledInstance(85, 85, java.awt.Image.SCALE_SMOOTH)
            /**
             * val scaled = BufferedImage(85, 85, BufferedImage.TYPE_INT_ARGB).apply {
            createGraphics().apply {
            drawImage(awtImage, 0, 0, null)
            dispose()
            }
            }.toImage()
             */

            /**
             * val scaled = BufferedImage(85, 85, BufferedImage.TYPE_INT_ARGB).apply {
            createGraphics().apply {
            drawImage(awtImage, 0, 0, null)
            dispose()
            }
            }.toImage()
             */
//            val image = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_AchievementIcon_O001.png").readBytes())

            val infoBg = async {
                Surface.makeRasterN32Premul(650, 920).apply {
                    canvas.apply {
                    }

                    File("infoBgMain.png").writeBytes(makeImageSnapshot().getBytes())
                }
            }

            Surface.makeRasterN32Premul(1505, 920).apply {
                canvas.apply {
                    // BackGround
                    drawBackGround()

                    // InfoBgMain
                    infoBg.await().draw(this, 50, 0, null)

                    // 名片
                    drawImageRect(
                        Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_NameCardPic_Kokomi_P.png").readBytes()),
                        Rect.makeXYWH(55f, 12f, 638f, 319f)
                    )

                    // 透明名片装饰线
                    drawLineNC()

                    // 头像框
                    drawAvatarFrame(100f, 375f, 300f)
                    // 200 205 180
                    drawLevelBox(85f, 420f, Color.makeRGB(165, 185, 130))
                    drawLevelBox(380f, 420f, Color.makeRGB(205, 185, 165))


                }

                File("bg.png").writeBytes(makeImageSnapshot().getBytes())
            }
        })
    }

    fun getTestImage(name: String) =
        Image.makeFromEncoded(this::class.java.getResource("/$name")!!.openStream().use { it.readBytes() })
}