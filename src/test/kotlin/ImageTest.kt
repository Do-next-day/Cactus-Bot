import icu.dnddl.plugin.genshin.draw.*
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.jetbrains.skia.*
import org.junit.Test
import org.laolittle.plugin.getBytes
import java.io.File
import kotlin.system.measureTimeMillis

internal class ImageTest {
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
                        infoBgMain.draw(this, 0, 0, null)
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
                    drawAvatarFrame(100f,375f, 300f)
                    // 200 205 180
                    drawLevelBox(85f, 420f, Color.makeRGB(165, 185, 130))
                    drawLevelBox(380f, 420f, Color.makeRGB(205,185,165))


                }

                File("bg.png").writeBytes(makeImageSnapshot().getBytes())
            }
        })
    }

    val infoBgMain by lazy {
        val infoBgWidth = 650
        val infoBgHeight = 920


        val infoBgMain = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_Bg.png").readBytes())

        infoBgMain.zoomAroundAtCornerWidth(56f,infoBgWidth, infoBgHeight).apply {
            canvas.apply {

                // 名片 比例:16/6
                val leftPadding = 15
                val topPadding = 14

                val nameCardWidth = infoBgWidth-leftPadding*2+4
                val nameCardHeight = nameCardWidth * 6 / 16
                println(nameCardHeight)
                drawImageRect(
                    Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_NameCardPic_Kokomi_P.png").readBytes()),
                    Rect.makeXYWH(leftPadding.toFloat(), topPadding.toFloat(), nameCardWidth.toFloat(), nameCardHeight.toFloat())
                )

                // 透明名片装饰线
                val lineNC = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_Line_NC.png").readBytes())
                val lineNCSf = lineNC.zoomTopAtPoint(47f, 48f, nameCardWidth, nameCardHeight)
                lineNCSf.draw(this, leftPadding, topPadding, Paint().setAlphaf(0.2f))

                // 头像框
                val avatarRadius = 100f
                drawAvatarFrame(avatarRadius, (nameCardWidth / 2 + leftPadding).toFloat(),(nameCardHeight - avatarRadius / 4))


                // 200 205 180
                drawLevelBox(35f, 420f, Color.makeRGB(165, 185, 130))
                drawLevelBox(330f, 420f, Color.makeRGB(205,185,165))

            }

            File("infoBg.png").writeBytes(makeImageSnapshot().getBytes())
        }
    }


    @Test
    fun testBg(): Unit = runBlocking{

        val bgWidth = 1500
        val bgHeight = 920
        val infoBgWidth = 650
        val infoBgHeight = 920


        val infoBgMain = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_Bg.png").readBytes())
        val infoBgMainSf = infoBgMain.zoomAroundAtCornerWidth(56f,infoBgWidth, infoBgHeight)
        infoBgMainSf.apply {
            canvas.apply {

                // 名片 比例:16/6
                val leftPadding = 15
                val topPadding = 14

                val nameCardWidth = infoBgWidth-leftPadding*2+4
                val nameCardHeight = nameCardWidth * 6 / 16
                println(nameCardHeight)
                drawImageClipHeight(
                    Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_NameCardPic_Kokomi_P.png").readBytes()),
                    Rect.makeXYWH(leftPadding.toFloat() - 1, topPadding.toFloat(), nameCardWidth.toFloat() + 2, nameCardHeight.toFloat())
                )

                // 透明名片装饰线
                val lineNC = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_Line_NC.png").readBytes())
                val lineNCSf = lineNC.zoomTopAtPoint(47f, 48f, nameCardWidth + 2, nameCardHeight)
                lineNCSf.draw(this, leftPadding - 1, topPadding, Paint().setAlphaf(0.2f))

                // 头像框
                val avatarRadius = 100f
                drawAvatarFrame(avatarRadius, (nameCardWidth / 2 + leftPadding).toFloat(),(nameCardHeight - avatarRadius / 4))


                // 200 205 180
                drawLevelBox(35f, 340f, Color.makeRGB(165, 185, 130))
                drawLevelBox(330f, 340f, Color.makeRGB(205,185,165))

                drawInfoBgA(30f, 430f, 210f)
                drawInfoBgA(330f, 430f, 210f)

                val p1 = Point(infoBgWidth / 2f, infoBgHeight /2f)
                val p2 = Point(infoBgWidth / 2f, infoBgHeight /2f)

                //drawPoint(p1.x, p2.y, Paint())
            }

            File("infoBg.png").writeBytes(makeImageSnapshot().getBytes())
        }

        val recordBGL = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_BGL.png").readBytes())
        val recordBGR = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_BGR.png").readBytes())
        val recordBGC = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_BGC.png").readBytes())

        val bg = Surface.makeRasterN32Premul(bgWidth, bgHeight).apply {
            canvas.apply {
                val centerWidth = 100
                val halfWidth = (bgWidth - centerWidth) / 2
                val bgPadding = Rect(0f,10f,0f,10f)
                val bglSf = recordBGL.zoomLeftAtPoint(46f, 48f, halfWidth, bgHeight, bgPadding)
                val bgrSf = recordBGR.zoomRightAtPoint(46f, 48f, halfWidth, bgHeight, bgPadding)
                val bgcSf = recordBGC.zoomVerticalAtPoint(20f, 36f, centerWidth, bgHeight, Rect(0f,12f,0f,12f))

                bglSf.draw(this, 0,0, Paint())
                bgcSf.draw(this, halfWidth,0, Paint())
                bgrSf.draw(this, halfWidth + centerWidth,0, Paint())


                infoBgMainSf.draw(this, 30,0,Paint())
            }
        }

        File("bg.png").writeBytes(bg.makeImageSnapshot().getBytes())

    }

    fun Canvas.drawBackGround(){
        val bgl = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_BGL.png").readBytes())
        val bgr = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_BGR.png").readBytes())
        val bgc = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_BGC.png").readBytes()) // 100 x 56

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

        val lineNC = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_Line_NC.png").readBytes())
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
        drawCircle(x, y, radius, Paint().apply {
            color = Color.makeRGB(210, 160, 120)
        })
        // 内边框
        drawCircle(x, y, radius - 10, Paint().apply {
            color = Color.makeRGB(220, 200, 165)
            mode = PaintMode.STROKE
            strokeWidth = 2f
        })
        // 中边框
        drawCircle(x, y, radius - 5, Paint().apply {
            mode = PaintMode.STROKE
            strokeWidth = 10f
            color = Color.makeRGB(240, 235, 227)
        })
        // 外边框
        drawCircle(x, y, radius, Paint().apply {
            mode = PaintMode.STROKE
            strokeWidth = 3.5f
            color = Color.makeRGB(220, 200, 165)
        })
    }

    fun Canvas.drawLevelBox(l: Float, t: Float, colorR: Int){
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
        drawImageRectTo(image, Rect.makeXYWH(40f, 0f, 40f, 118f), l+ 40+ len, t)
    }

    @Test
    fun scaleImage() {
        val image = Image.makeFromEncoded(this::class.java.getResource("/UI_HomeworldModule_3_Pic.png")!!.openStream().readBytes())

        Surface.makeRasterN32Premul(512, 256).apply {
            canvas.apply {
                drawImageRect(image, Rect(0f, 0f, image.width.toFloat(), image.height.toFloat()), Rect(0f, 0f, 512f, 256f), FilterMipmap(FilterMode.LINEAR, MipmapMode.NEAREST) , Paint().apply {
                    isAntiAlias = true
                }, false)
            }

            File("scale_test.png").writeBytes(makeImageSnapshot().getBytes())
        }
    }
}