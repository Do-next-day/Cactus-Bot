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

            val infoBgMain = async {
                Surface.makeRasterN32Premul(650, 920).apply {
                    canvas.apply {
                        drawInfoBgMain(0f, 8f, 600f, 810f)
                        drawRect(Rect(40f, 45f, 610f, 880f), Paint().apply {
                            color = Color.makeRGB(240, 235, 227)
                        })
                    }
                }
            }

            Surface.makeRasterN32Premul(1505, 920).apply {
                canvas.apply {
                    // BackGround
                    drawBackGround()

                    // InfoBgMain
                    infoBgMain.await().draw(this, 50, 0, null)

                    // 名片
                    drawImageRect(
                        Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_NameCardPic_Kokomi_P.png").readBytes()),
                        Rect.makeXYWH(55f, 12f, 638f, 319f)
                    )

                    // 透明名片装饰线
                    drawLineNC()

                    // 头像框
                    drawAvatarFrame(375f, 300f)
                    // 200 205 180
                    drawLevelBox(85f, 420f, Color.makeRGB(165, 185, 130))
                    drawLevelBox(380f, 420f, Color.makeRGB(205,185,165))


                }

                File("bg.png").writeBytes(makeImageSnapshot().getBytes())
            }
        })
    }


    @Test
    fun testBg(): Unit = runBlocking{

        val infoBgMainWidth = 650
        val infoBgMainHeight = 920

        val infoBgMain = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_Bg.png").readBytes())
        val infoBgMainSf = infoBgMain.zoomAroundAtCornerWidth(56f,infoBgMainWidth, infoBgMainHeight)
        infoBgMainSf.apply {
            canvas.apply {
                // 名片
                drawImageRect(
                    Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_NameCardPic_Kokomi_P.png").readBytes()),
                    Rect.makeXYWH(14f, 13f, infoBgMainWidth+4-14f*2, 319f)
                )

                // 透明名片装饰线
                val lineNC = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_Line_NC.png").readBytes())
                val lineNCSf = lineNC.zoomTopAtPoint(47f, 48f, infoBgMainWidth+4-14*2, 319)
                lineNCSf.draw(this, 14,13, Paint().setAlphaf(0.2f))

                // 头像框
                drawAvatarFrame(325f, 300f)
                // 200 205 180
                drawLevelBox(35f, 420f, Color.makeRGB(165, 185, 130))
                drawLevelBox(330f, 420f, Color.makeRGB(205,185,165))
            }
        }

        val bgl = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_BGL.png").readBytes())
        val bgr = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_BGR.png").readBytes())
        val bgc = Image.makeFromEncoded(File("src/main/resources/GenshinRecord/UI_FriendInfo_BGC.png").readBytes())
        val bgPadding = Rect(0f,10f,0f,10f)
        val bglSf = bgl.zoomLeftAtPoint(46f, 48f, 702, 920, bgPadding)
        val bgrSf = bgr.zoomRightAtPoint(46f, 48f, 702, 920, bgPadding)
        val bgcSf = bgc.zoomVerticalAtPoint(20f, 36f, 100, 920, Rect(0f,12f,0f,12f))

        val bg = Surface.makeRasterN32Premul(1505, 920).apply {
            canvas.apply {
                bglSf.draw(this, 0,0, Paint())
                bgcSf.draw(this, 702,0, Paint())
                bgrSf.draw(this, 802,0, Paint())

                infoBgMainSf.draw(this, 50,0,Paint())
            }
        }

        File("bg.png").writeBytes(bg.makeImageSnapshot().getBytes())

    }


    fun Image.zoomLeftAtPoint(
        verticalTopPoint: Float,
        verticalBottomPoint: Float,
        dstWidth: Int,
        dstHeight: Int,
        dstPadding: Rect = Rect(0f,0f,0f,0f),
        srcPadding: Rect = Rect(0f,0f,0f,0f)
    ) = zoomAroundAtPoint(
        (this.width - 2).toFloat(),
        (this.width - 1).toFloat(),
        verticalTopPoint,
        verticalBottomPoint,
        dstWidth,
        dstHeight,
        dstPadding,
        srcPadding,
    )

    fun Image.zoomRightAtPoint(
        verticalTopPoint: Float,
        verticalBottomPoint: Float,
        dstWidth: Int,
        dstHeight: Int,
        dstPadding: Rect = Rect(0f,0f,0f,0f),
        srcPadding: Rect = Rect(0f,0f,0f,0f)
    ) = zoomAroundAtPoint(
        1f,
        2f,
        verticalTopPoint,
        verticalBottomPoint,
        dstWidth,
        dstHeight,
        dstPadding,
        srcPadding
    )

    fun Image.zoomTopAtPoint(
        horizontalLeftPoint: Float,
        horizontalRightPoint: Float,
        dstWidth: Int,
        dstHeight: Int,
        dstPadding: Rect = Rect(0f,0f,0f,0f),
        srcPadding: Rect = Rect(0f,0f,0f,0f)
    ) = zoomAroundAtPoint(
        horizontalLeftPoint,
        horizontalRightPoint,
        (this.height - 2).toFloat(),
        (this.height - 1).toFloat(),
        dstWidth,
        dstHeight,
        dstPadding,
        srcPadding,
    )

    fun Image.zoomVerticalAtPoint(
        verticalTopPoint: Float,
        verticalBottomPoint: Float,
        dstWidth: Int,
        dstHeight: Int,
        dstPadding: Rect = Rect(0f,0f,0f,0f),
        srcPadding: Rect = Rect(0f,0f,0f,0f)
    ) = zoomAroundAtPoint(
        this.height.toFloat(),
        this.height.toFloat(),
        verticalTopPoint,
        verticalBottomPoint,
        dstWidth,
        dstHeight,
        dstPadding,
        srcPadding
    )

    fun Image.zoomAroundAtCornerWidth(
        cornerWidth: Float,
        dstWidth: Int,
        dstHeight: Int,
        dstPadding: Rect = Rect(0f,0f,0f,0f),
        srcPadding: Rect = Rect(0f,0f,0f,0f)
    ) = zoomAroundAtPoint(
        cornerWidth,
        this.width - cornerWidth,
        cornerWidth,
        this.height - cornerWidth,
        dstWidth,
        dstHeight,
        dstPadding,
        srcPadding
    )

    fun Image.zoomAroundAtPoint(
        horizontalLeftPoint: Float,
        horizontalRightPoint: Float,
        verticalTopPoint: Float,
        verticalBottomPoint: Float,
        dstWidth: Int,
        dstHeight: Int,
        dstPadding: Rect = Rect(0f,0f,0f,0f),
        srcPadding: Rect = Rect(0f,0f,0f,0f)
    ): Surface = zoomAroundAtRect(
        Rect(srcPadding.left, srcPadding.top, horizontalLeftPoint, verticalTopPoint),
        Rect(horizontalRightPoint, srcPadding.top, this.width.toFloat() - srcPadding.right, verticalTopPoint),
        Rect(horizontalRightPoint, verticalBottomPoint, this.width.toFloat() - srcPadding.right, this.height.toFloat() - srcPadding.bottom),
        Rect(srcPadding.left, verticalBottomPoint, horizontalLeftPoint, this.height.toFloat() - srcPadding.bottom),

        Rect(horizontalLeftPoint, srcPadding.top, horizontalRightPoint, verticalTopPoint),
        Rect(horizontalLeftPoint, verticalBottomPoint, horizontalRightPoint, this.height.toFloat() - srcPadding.bottom),
        Rect(srcPadding.left, verticalTopPoint, horizontalLeftPoint, verticalBottomPoint),
        Rect(horizontalRightPoint, verticalTopPoint, this.width.toFloat() - srcPadding.right, verticalBottomPoint),
        dstWidth,
        dstHeight,
        dstPadding
    )


    /**
     *
     */
    fun Image.zoomAroundAtRect(
        leftTopCornerRect: Rect,
        rightTopCornerRect: Rect,
        rightBottomCornerRect: Rect,
        leftBottomCornerRect: Rect,
        topHorizontalRect: Rect,
        bottomHorizontalRect: Rect,
        leftVerticalRect: Rect,
        rightVerticalRect: Rect,
        dstWidth: Int,
        dstHeight: Int,
        padding: Rect = Rect(0f,0f,0f,0f),
    ): Surface = Surface.makeRasterN32Premul(dstWidth, dstHeight).apply surface@{
        canvas.apply {

            // 左上角
            drawImageRect(
                this@zoomAroundAtRect, leftTopCornerRect,
                Rect.makeXYWH(padding.left, padding.top, leftTopCornerRect.width, leftTopCornerRect.height)
            )
            // 右上角
            drawImageRect(
                this@zoomAroundAtRect, rightTopCornerRect,
                Rect.makeXYWH(
                    this@surface.width - padding.right - rightTopCornerRect.width,
                    padding.top,
                    rightTopCornerRect.width,
                    rightTopCornerRect.height
                )
            )
            // 右下角
            drawImageRect(
                this@zoomAroundAtRect, rightBottomCornerRect,
                Rect.makeXYWH(
                    this@surface.width - padding.right - rightBottomCornerRect.width,
                    this@surface.height - padding.bottom - rightBottomCornerRect.height,
                    rightBottomCornerRect.width,
                    rightBottomCornerRect.height
                )
            )
            // 左下角
            drawImageRect(
                this@zoomAroundAtRect, leftBottomCornerRect,
                Rect.makeXYWH(
                    padding.left,
                    this@surface.height - padding.bottom - leftBottomCornerRect.height,
                    leftBottomCornerRect.width,
                    leftBottomCornerRect.height
                )
            )

            // 上水平
            drawImageRect(
                this@zoomAroundAtRect, topHorizontalRect,
                Rect.makeXYWH(
                    padding.left + leftTopCornerRect.width,
                    padding.top,
                    this@surface.width - padding.left - padding.right - leftTopCornerRect.width - rightTopCornerRect.width,
                    topHorizontalRect.height
                )
            )
            // 下水平
            drawImageRect(
                this@zoomAroundAtRect, bottomHorizontalRect,
                Rect.makeXYWH(
                    padding.left + leftTopCornerRect.width,
                    this@surface.height - padding.bottom - bottomHorizontalRect.height,
                    this@surface.width - padding.left - padding.right - leftTopCornerRect.width - rightTopCornerRect.width,
                    bottomHorizontalRect.height
                )
            )
            // 左竖直
            drawImageRect(
                this@zoomAroundAtRect, leftVerticalRect,
                Rect.makeXYWH(
                    padding.left,
                    padding.top + leftTopCornerRect.height,
                    leftVerticalRect.width,
                    this@surface.height - padding.top - padding.bottom - leftTopCornerRect.height - leftBottomCornerRect.height
                )
            )
            // 右竖直
            drawImageRect(
                this@zoomAroundAtRect, rightVerticalRect,
                Rect.makeXYWH(
                    this@surface.width - padding.right - rightVerticalRect.width,
                    padding.top + rightTopCornerRect.height,
                    rightVerticalRect.width,
                    this@surface.height - padding.top - padding.bottom - rightTopCornerRect.height - rightBottomCornerRect.height
                )
            )

            // 中心
            drawImageRect(
                this@zoomAroundAtRect,
                Rect(leftVerticalRect.right, topHorizontalRect.height, rightVerticalRect.left, bottomHorizontalRect.top),
                Rect.makeXYWH(
                    padding.left + leftVerticalRect.width,
                    padding.top + topHorizontalRect.height,
                    this@surface.width - padding.left - padding.right - leftTopCornerRect.width - rightTopCornerRect.width,
                    this@surface.height - padding.top - padding.bottom - rightTopCornerRect.height - rightBottomCornerRect.height
                )
            )
        }
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

    fun Canvas.drawAvatarFrame(x: Float, y: Float) {
        // (30 + 600) / 2
        drawCircle(x, y, 100f, Paint().apply {
            color = Color.makeRGB(210, 160, 120)
        }) // 中部实心圆
        drawCircle(x, y, 90f, Paint().apply {
            color = Color.makeRGB(220, 200, 165)
            mode = PaintMode.STROKE
            strokeWidth = 2f
        }) // 内边框
        drawCircle(x, y, 95f, Paint().apply {
            mode = PaintMode.STROKE
            strokeWidth = 10f
            color = Color.makeRGB(240, 235, 227)
        }) // 中边框
        drawCircle(x, y, 100f, Paint().apply {
            mode = PaintMode.STROKE
            strokeWidth = 3.5f
            color = Color.makeRGB(220, 200, 165)
        }) // 外边框
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
            color = Color.makeRGB(213, 191, 145)
        })
        drawRect(Rect.makeXYWH(l + 40, t + 3, len, 112f), Paint().apply {
            color = Color.makeRGB(240, 240, 235)
        })
        drawImageRect(image, Rect.makeXYWH(0f, 0f, 40f, 118f), Rect.makeXYWH(l, t, 40f, 118f))
        drawImageRect(image, Rect.makeXYWH(40f, 0f, 40f, 118f), Rect.makeXYWH(l + 40 + len, t, 40f, 118f))
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