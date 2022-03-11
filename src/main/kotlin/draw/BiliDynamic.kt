package icu.dnddl.plugin.genshin.draw

import icu.dnddl.plugin.genshin.CactusConfig.image
import icu.dnddl.plugin.genshin.api.bilibili.data.*
import icu.dnddl.plugin.genshin.util.cacheFolder
import icu.dnddl.plugin.genshin.util.getOrDownload
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import org.jetbrains.skia.*
import org.laolittle.plugin.getBytes
import java.awt.BasicStroke
import java.awt.SystemColor.text
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.io.File

private const val cardWidth = 800
private const val margin = 20

//fun String.dynamicImage(type: Int): Image {
//    return when (type) {
//        DynamicType.REPLY -> decode<DynamicReply>()
//        DynamicType.PICTURE -> decode<DynamicPicture>()
//        DynamicType.TEXT -> decode<DynamicText>()
//        DynamicType.VIDEO -> decode<DynamicVideo>()
//        DynamicType.ARTICLE -> decode<DynamicArticle>()
//        DynamicType.MUSIC -> decode<DynamicMusic>()
//        DynamicType.EPISODE -> decode<DynamicEpisode>()
//        DynamicType.SKETCH -> decode<DynamicSketch>()
//        DynamicType.LIVE, DynamicType.LIVE_ING -> decode<DynamicLive>()
//        else -> DynamicNull()
//    }
//}

suspend fun makeDynamicImage(){
    Surface.makeRasterN32Premul(cardWidth, cardWidth * 2).apply {
        canvas.apply {
            drawDynamicBg(Rect(0f,0f, cardWidth.toFloat(), cardWidth.toFloat() * 2))
//            val f = Font(Typeface.makeFromName("Noto Sans SC", FontStyle.NORMAL), 35f)
//            val t = TextLine.make("cccccccccc", f)
            val tt = "[原神_哼]下午3点半！开始[原神_欸嘿]\n" +
                    "原神~！！！！\n" +
                    "新活动来了！！！[原神_嗯]\n" +
                    "我爱可莉！！！！！！！！！"
            drawImage(makeTextContent(tt), margin.toFloat(), margin.toFloat(),null)

            val images = listOf(
                    DynamicPictureInfo(1080,null,"https://i0.hdslb.com/bfs/album/648fb2527f49fd42c6ecbf991151e1593e7225ad.jpg",1920),
                    DynamicPictureInfo(816,null,"https://i0.hdslb.com/bfs/album/0ae6fee9eaeafe614377cf5451e78c2430d5a6e4.gif",499),
                    DynamicPictureInfo(816,null,"https://i0.hdslb.com/bfs/album/56c135d05c0d4a77964db04a07f039d7fe945f14.gif",499),
                    DynamicPictureInfo(816,null,"https://i0.hdslb.com/bfs/album/1971358d4d71ded8c1b287c7377a32c397190490.gif",499),
                    DynamicPictureInfo(816,null,"https://i0.hdslb.com/bfs/album/554027b1f38ad88040e315cdf55ee98ed5a20335.gif",499),
                    DynamicPictureInfo(816,null,"https://i0.hdslb.com/bfs/album/2180d689ad3de225f70e37f6ae3e953f583b012a.gif",499),
                    DynamicPictureInfo(816,null,"https://i0.hdslb.com/bfs/album/d009f31357f049f7f8134e4e74e208db4284c2eb.gif",499),
                )

            drawImage(makeImageContent(images), 0f, 200f ,null)


        }
        File("dynamic.png").writeBytes(makeImageSnapshot().getBytes())
    }
}

private fun Canvas.drawDynamicBg(rect: Rect){
    drawRect(rect, Paint().apply {
        shader = Shader.makeLinearGradient(
            Point(rect.left,rect.top),
            Point(rect.right,rect.bottom),
            intArrayOf(
                0xFFD16BA5.toInt(), 0xFFC777B9.toInt(), 0xFFBA83CA.toInt(), 0xFFAA8FD8.toInt(),
                0xFF9A9AE1.toInt(), 0xFF8AA7EC.toInt(), 0xFF79B3F4.toInt(), 0xFF69BFF8.toInt(),
                0xFF52CFFE.toInt(), 0xFF41DFFF.toInt(), 0xFF46EEFA.toInt(), 0xFF5FFBF1.toInt()
            )
        )
    })

    val im = getImageFromResource("/GenshinRecord/UI_NameCardPic_Kokomi_P.png")

    drawImage(im, 0f, 0f, null)

    val rr = RRect.makeLTRB(
        rect.left + margin,
        rect.top + margin,
        rect.right - margin,
        rect.bottom - margin,
        10f)

    drawRRect(rr, Paint().apply {
            imageFilter = ImageFilter.makeBlur(5f,5f,
                FilterTileMode.DECAL,
                ImageFilter.makeImage(
                    im,
                    Rect.makeXYWH(50f,50f,cardWidth.toFloat() - 100, cardWidth.toFloat() - 100),
                    Rect.makeXYWH(50f,50f,cardWidth.toFloat() - 100, cardWidth.toFloat() - 100),
                    SamplingMode.DEFAULT
                )
            )
        }
    )


    /**
     * 黑色
     */
    drawRRect(rr, Paint().apply {
        color = Color.BLACK
        alpha = 100
    })
    drawRectShadowAntiAlias(rr.inflate(1f),10f,10f,30f,10f,Color.makeARGB(85, 0,0,0))

    /**
     * 白色
     */
//    drawRRect(rr, Paint().apply {
//        color = Color.WHITE
//        alpha = 90
//    })
//    drawRectShadowAntiAlias(rr,10f,10f,30f,10f,Color.makeARGB(50, 0,0,0))

}

private fun makeTextContent(text: String): Image {

    var textY = 35f

    val surface = Surface.makeRasterN32Premul(cardWidth - margin * 2 - 20, 2000).apply {
        canvas.apply {
            val font = Font(Typeface.makeFromName("Noto Sans SC", FontStyle.NORMAL), 25f)
            val paint = Paint().apply {
                color = Color.WHITE
            }
            val emojiMap: MutableMap<String, Image> = mutableMapOf()

            var textX = 35f
            var emojiStart = 0
            var emojiFlag = false

            for ((i, c) in text.withIndex()) {
                if (c == '[') {
                    emojiStart = i
                    emojiFlag = true
                } else if (c == '\n') {
                    textX = 35f
                    textY += 35f
                } else if (c == ']') {
                    val emojiText = text.substring(emojiStart, i + 1)
                    textX += try {
                        val emoji = emojiMap[emojiText] ?: throw Exception()
                        drawImage(emoji, textX, textY - 23f, null)
                        35f
                    } catch (e: Exception) {
                        val t = TextLine.make("\uD83D\uDE13", font)
                        drawTextLine(t, textX + 2f, textY, paint)
                        t.width + 4
                    }
                    emojiFlag = false
                } else if (!emojiFlag) {
                    val t = TextLine.make(c.toString(), font)
                    if (textX > 740) {
                        textX = 35f
                        textY += 35f
                    }
                    if (c.toString().matches("[\u4e00-\u9fa5]".toRegex())) {
                        drawTextLine(t, ++textX, textY, paint)
                        textX++
                    } else {
                        drawTextLine(t, textX, textY, paint)
                    }
                    textX += t.width.toInt()
                }
                if (textY > 1900) {
                    textX += 200
                    drawTextLine(TextLine.make("!!文字过长, 后续已省略!!", font), textX, textY, paint)
                    break
                }
            }


        }
    }

    return surface.makeImageSnapshot(IRect.makeWH(surface.width, (textY + 15).toInt()))!!
}

private suspend fun makeImageContent(pictures: List<DynamicPictureInfo>): Image {

    var picH = 20

    val surface = Surface.makeRasterN32Premul(cardWidth, 1000).apply {
        canvas.apply {
            val picArc = 20

            val picPadding = 10
            var picX = margin

            val imgRowCount = if (pictures.size >= 3) 3 else pictures.size
            var picWidth = ((cardWidth - margin * 2) - picPadding * (imgRowCount - 1)) / imgRowCount
            var picHeight = picWidth
            for ((i, pic) in pictures.withIndex()) {
                if (pictures.size == 1) {
                    if (pic.width < picWidth) {
                        picHeight = if (pic.height > picWidth) picWidth else pic.height
                        picWidth = pic.width
                    } else if (pic.height * picWidth / pic.width > picWidth) {
                        picHeight = picWidth
                    } else {
                        picHeight = pic.height * picWidth / pic.width
                    }
                }
                val rr = RRect.makeXYWH(picX.toFloat(), picH.toFloat(), picWidth.toFloat(), picHeight.toFloat(), picArc.toFloat())
                save()
                clipRRect(rr,true)

                val image = Image.makeFromEncoded(getOrDownloadA(imgApi(pic.source, picWidth, picHeight)))

                //Rect.makeXYWH(picX.toFloat(), picH.toFloat(), picWidth.toFloat(), picHeight.toFloat())
                drawImageRect(image, Rect.makeXYWH(picX.toFloat(), picH.toFloat(), picWidth.toFloat(), picHeight.toFloat()))
                restore()
//                imgG2.stroke = BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
//                imgG2.color = java.awt.Color.WHITE
//                imgG2.drawRoundRect(picX, picH, picWidth, picHeight, picArc, picArc)

                picX += (picPadding + picWidth)
                if (i % 3 == 2) {
                    picH += if (i != pictures.size - 1) picPadding + picHeight else 0
                    picX = margin
                }
            }
            picH += picHeight
        }
    }

    return surface.makeImageSnapshot(IRect.makeWH(surface.width, picH + 20))!!

}

fun Canvas.drawRectShadowAntiAlias(r: Rect, dx: Float, dy: Float, blur: Float, spread: Float, color: Int): Canvas {
    val insides = r.inflate(-1f)
    if (!insides.isEmpty) {
        save()
        if (insides is RRect) clipRRect(insides, ClipMode.DIFFERENCE, true) else clipRect(insides, ClipMode.DIFFERENCE, true)
        drawRectShadowNoclip(r, dx, dy, blur, spread, color)
        restore()
    } else drawRectShadowNoclip(r, dx, dy, blur, spread, color)
    return this
}

private fun imgApi(imgUrl: String, width: Int, height: Int): String = "${imgUrl}@${width}w_${height}h_1e_1c.png"

suspend fun getOrDownloadA(url: String, block: HttpRequestBuilder.() -> Unit = {}): ByteArray {
    HttpClient(OkHttp).use { client ->
        client.get<ByteArray>(url, block).also { data ->
            return data
        }
    }
}