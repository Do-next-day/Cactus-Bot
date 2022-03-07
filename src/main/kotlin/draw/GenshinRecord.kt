package icu.dnddl.plugin.genshin.draw

import icu.dnddl.plugin.genshin.api.genshin.data.GenshinRecord
import org.jetbrains.skia.*

private const val bgWidth = 1500
private const val bgHeight = 920
private const val infoBgWidth = 650
private const val infoBgHeight = 920


fun GenshinRecord.infoImage(): Image{
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

fun GenshinRecord.recordInfo(): Surface{
    return Surface.makeRasterN32Premul(infoBgWidth, infoBgHeight).apply {
        canvas.apply {
            infoBg.draw(this, 0, 0, Paint())

            // 名片 比例:16/6
            val leftPadding = 15
            val topPadding = 14

            val nameCardWidth = infoBgWidth-leftPadding*2+4
            val nameCardHeight = nameCardWidth * 6 / 16
            drawImageRect(
                getNameCardImage(),
                Rect.makeXYWH(leftPadding.toFloat(), topPadding.toFloat(), nameCardWidth.toFloat(), nameCardHeight.toFloat())
            )

            // 透明名片装饰线
            val lineNCSf = lineNC.zoomTopAtPoint(47f, 48f, nameCardWidth, nameCardHeight)
            lineNCSf.draw(this, leftPadding, topPadding, Paint().setAlphaf(0.2f))

            // 头像框
            val avatarRadius = 100f
            drawAvatarFrame(avatarRadius, (nameCardWidth / 2 + leftPadding).toFloat(),(nameCardHeight - avatarRadius / 4))


            // 200 205 180
            drawLevelBox(35f, 420f, Color.makeRGB(165, 185, 130))
            drawLevelBox(330f, 420f, Color.makeRGB(205,185,165))

            // todo

        }
    }
}

private fun getNameCardImage(): Image{
    return getImageFromResource("$resourceFolder/UI_NameCardPic_Kokomi_P.png")
}

private val infoBg: Surface by lazy {
    infoBG.zoomAroundAtCornerWidth(56f,infoBgWidth, infoBgHeight)
}

private val recordBg: Surface by lazy {
    Surface.makeRasterN32Premul(bgWidth,bgHeight).apply {
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
        }
    }
}

private fun Canvas.drawAvatarFrame(radius: Float, x: Float, y: Float) {
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

private fun Canvas.drawLevelBox(l: Float, t: Float, colorR: Int){
    drawRRect(RRect.makeComplexXYWH(l, t, 285f, 52f, floatArrayOf(1f)), Paint().apply {
        color = colorR
    })

    drawRRect(RRect.makeComplexXYWH(l, t, 285f, 52f, floatArrayOf(1f)), Paint().apply {
        mode = PaintMode.STROKE
        strokeWidth = 1.25f
        color = Color.makeRGB(200, 205, 180)
    })
}

private const val resourceFolder = "/GenshinRecord"
private val recordBGL = getImageFromResource("$resourceFolder/UI_FriendInfo_BGL.png")
private val recordBGR = getImageFromResource("$resourceFolder/UI_FriendInfo_BGR.png")
private val recordBGC = getImageFromResource("$resourceFolder/UI_FriendInfo_BGC.png")

private val infoBG = getImageFromResource("$resourceFolder/UI_FriendInfo_Bg.png")
private val lineNC = getImageFromResource("$resourceFolder/UI_FriendInfo_Line_NC.png")