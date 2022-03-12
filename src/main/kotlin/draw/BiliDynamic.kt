package icu.dnddl.plugin.genshin.draw

import com.vdurmont.emoji.EmojiParser
import icu.dnddl.plugin.genshin.api.bilibili.data.*
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.skia.*
import org.laolittle.plugin.getBytes
import java.io.File


// 动态图片宽度
private const val cardWidth = 800
// 动态卡片外边距
private const val cardMargin = 20
// 内容外边距
private const val contentMargin = 40
// 内容宽度
private const val contentWidth = cardWidth - contentMargin * 2


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
    Surface.makeRasterN32Premul(cardWidth, cardWidth).apply {
        canvas.apply {
            drawDynamicBg(RRect.makeXYWH(0f,0f, cardWidth.toFloat(), cardWidth.toFloat(), 10f))

            // Noto Sans SC  HarmonyOS Sans SC
            val font = Font(Typeface.makeFromName("Noto Sans SC", FontStyle.NORMAL), 30f)
            val paint = Paint().apply {
                color = Color.WHITE
                isAntiAlias = true
                isDither = true
                mode = PaintMode.FILL
            }

            val tt = "[原神_哼]下午3点半！开始[原神_欸嘿]开始开始AAAAAAA开始开始BBBBBBBBBBBBBBBBBB原神原神原神原神原神\n" +
                    "原神~！！！！\n" +
                    "新活动来了！！！[原神_嗯]\n" +
                    "我爱可莉！！！！！！！！！"
            drawImage(makeTextContent(tt, font, paint), 0f, 0f,null)
//            drawImage(makeTextContent(tt), contentMargin.toFloat(), contentMargin.toFloat(),null)

            val images = listOf(
                    DynamicPictureInfo(1080,null,"https://i0.hdslb.com/bfs/album/648fb2527f49fd42c6ecbf991151e1593e7225ad.jpg",1920),
                    DynamicPictureInfo(816,null,"https://i0.hdslb.com/bfs/album/0ae6fee9eaeafe614377cf5451e78c2430d5a6e4.gif",499),
                    DynamicPictureInfo(816,null,"https://i0.hdslb.com/bfs/album/56c135d05c0d4a77964db04a07f039d7fe945f14.gif",499),
//                    DynamicPictureInfo(816,null,"https://i0.hdslb.com/bfs/album/1971358d4d71ded8c1b287c7377a32c397190490.gif",499),
//                    DynamicPictureInfo(816,null,"https://i0.hdslb.com/bfs/album/554027b1f38ad88040e315cdf55ee98ed5a20335.gif",499),
//                    DynamicPictureInfo(816,null,"https://i0.hdslb.com/bfs/album/2180d689ad3de225f70e37f6ae3e953f583b012a.gif",499),
//                    DynamicPictureInfo(816,null,"https://i0.hdslb.com/bfs/album/d009f31357f049f7f8134e4e74e208db4284c2eb.gif",499),
                )

//            drawImage(makeImageContent(images), contentMargin.toFloat(), 200f ,null)


//            drawImage(makeVideoContent("https://i0.hdslb.com/bfs/archive/5c20fc634ca754acc6b48a445a2980b9a4942107.jpg",
//                "趣味视频征集活动今日",
//                "视频内容不限，包括但不限于打法攻略、创意玩法等内容视频内容不限，包括但不限于打法攻略、创意玩法等内容",
//                "视频"), contentMargin.toFloat(), 200f ,null)

//            drawImage(makeArticleContent(listOf("https://i0.hdslb.com/bfs/archive/5c20fc634ca754acc6b48a445a2980b9a4942107.jpg"),
//                "趣味视频征集活动今日",
//                "视频内容不限，包括但不限于打法攻略、创意玩法等内容视频内容不限，包括但不限于打法攻略、创意玩法等内容",),
//                contentMargin.toFloat(), 200f ,null)


        }
        File("dynamic.png").writeBytes(makeImageSnapshot().getBytes())
    }
}

private fun Canvas.drawDynamicBg(rect: RRect){
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


    val rr = RRect.makeComplexLTRB(
        rect.left + cardMargin,
        rect.top + contentMargin,
        rect.right - cardMargin,
        rect.bottom - cardMargin,
        rect.radii
    )

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

private suspend fun makeTextContent(text: String, font: Font, paint: Paint, emojiList: List<EmojiDetails>? = null): Image {

    var msgText = text
    val lineHeight = font.size * 1.5f
    var textY = lineHeight

    val surface = Surface.makeRasterN32Premul(contentWidth, 2000).apply {
        canvas.apply {

            val emojiMap: MutableMap<String, Image> = mutableMapOf()

            emojiList?.forEach {
                if (!emojiMap.containsKey(it.emojiName)) {
                    emojiMap[it.emojiName] = scaleImage(getImageOrDownload(it.url), Rect(0f,0f,30f,30f))
                }
            }

            msgText = EmojiParser.parseFromUnicode(msgText) { e ->
                val emojis = e.emoji.htmlHexadecimal.split(";").filter{ it.isNotEmpty() }.map{ it.substring(3) }.toList()
                val emoji = emojis.joinToString("-")
                if (!emojiMap.containsKey(emoji)) {
                    var emojiImg: Image? = null

                    runCatching {
                        runBlocking {
                            emojiImg = getImageOrDownload("https://twemoji.maxcdn.com/36x36/$emoji.png")
                        }
                    }.onFailure {
//                        logger.warning("获取 $emoji emoji失败")
                        return@parseFromUnicode e.emoji.unicode
                    }

                    emojiMap["[$emoji]"] = scaleImage(emojiImg!!, Rect(0f,0f,30f,30f))
                }
                "[$emoji]"
            }

            var textX = 0f
            var emojiStart = 0
            var emojiFlag = false

            for ((i, c) in msgText.withIndex()) {
                if (c == '[') {
                    emojiStart = i
                    emojiFlag = true
                } else if (c == '\n') {
                    textX = 0f
                    textY += lineHeight
                } else if (c == ']') {
                    val emojiText = msgText.substring(emojiStart, i + 1)
                    textX += try {
                        val emoji = emojiMap[emojiText] ?: throw Exception()
                        drawImage(emoji, textX, textY - 23f, null)
                        35f
                    } catch (e: Exception) {
                        val t = TextLine.make("\uD83D\uDE13", font)
                        drawTextLine(t, textX + 2f, textY, paint)
                        t.width + 5
                    }
                    emojiFlag = false
                } else if (!emojiFlag) {
                    val t = TextLine.make(c.toString(), font)
                    if (textX > 740) {
                        textX = 0f
                        textY += lineHeight
                    }
                    drawTextLine(t, textX, textY, paint)
                    if (c.toString().matches("[\u4e00-\u9fa5]".toRegex())) {
                        drawTextLine(t, textX+2, textY, paint)
                        textX+=4
                    } else {
                        drawTextLine(t, ++textX, textY, paint)
                        textX++
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

    val surface = Surface.makeRasterN32Premul(contentWidth, 1000).apply {
        canvas.apply {
            val picArc = 20

            val picPadding = 10
            var picX = 0

            val imgRowCount = if (pictures.size >= 3) 3 else pictures.size
            var picWidth = (contentWidth - picPadding * (imgRowCount - 1)) / imgRowCount
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

                val image = getImageOrDownload(imgApi(pic.source, picWidth, picHeight))

                drawImageRRect(image, rr)

                picX += (picPadding + picWidth)
                if (i % 3 == 2) {
                    picH += if (i != pictures.size - 1) picPadding + picHeight else 0
                    picX = 0
                }
            }
            picH += picHeight
        }
    }

    return surface.makeImageSnapshot(IRect.makeWH(surface.width, picH + 20))!!
}

private suspend fun makeVideoContent(coverUrl: String, title: String, desc: String, tag: String = ""): Image {

    val picArc = 10
    val imgHeight = 360
    val cardHeight = 470

    return Surface.makeRasterN32Premul(contentWidth, cardHeight).apply {
        canvas.apply {

            val rr = RRect.makeXYWH(0f, 0f, contentWidth.toFloat(), cardHeight.toFloat(), picArc.toFloat())

            drawRRect(rr, Paint().apply {
                color = Color.WHITE
            })

            val image = getImageOrDownload(imgApi(coverUrl, contentWidth, imgHeight))

            drawImageRRect(image, rr)

            if (tag != "") {
                drawRRect(RRect.makeXYWH(663f, 30f, 65f, 32f, 5f), Paint().apply {
                    color = Color.makeRGB(251, 114, 153)
                })

                val font = Font(Typeface.makeFromName("Noto Sans SC", FontStyle.NORMAL), 25f)

                drawTextLine(TextLine.make(tag, font), 673f, 54f, Paint().apply {
                    color = Color.WHITE
                })
            }

            val font = Font(Typeface.makeFromName("Noto Sans SC", FontStyle.NORMAL), 20f)

            val textY = writeText(title, 30f, imgHeight + 40f, contentWidth - 30, 1, font, Paint().apply {
                color = Color.BLACK
            })
            writeText(desc, 30f, textY + 25f, contentWidth - 30, 3, font.makeWithSize(16f), Paint().apply {
                color = Color.makeRGB(148, 147, 147)
            })

//            drawRectShadowAntiAlias(rr.inflate(1f),10f,10f,30f,10f,Color.makeARGB(85, 0,0,0))

        }
    }.makeImageSnapshot()
}

private suspend fun makeArticleContent(imageUrls: List<String>, title: String, desc: String): Image{

    val picArc = 10
    val imgHeight = 170
    val cardHeight = 275


    return Surface.makeRasterN32Premul(contentWidth,cardHeight).apply {
        canvas.apply {

            val rr = RRect.makeXYWH(0f,0f, contentWidth.toFloat(), cardHeight.toFloat(), picArc.toFloat())
            drawRRect(rr, Paint().apply {
                color = Color.WHITE
            })

            if (imageUrls.size == 3) {
                var imgX = 0f
                val imgW = contentWidth / 3 - 4
                imageUrls.forEach {
                    val rrr = RRect.makeXYWH(imgX, 0f, imgW.toFloat(), imgHeight.toFloat(), picArc.toFloat())
                    val image = getImageOrDownload(imgApi(it, imgW, imgHeight))

                    drawImageRRect(image, rrr)

                    imgX += cardWidth / 3 + 2
                }
            } else {
                val rrr = RRect.makeXYWH(0f,0f, contentWidth.toFloat(), imgHeight.toFloat(), picArc.toFloat())
                val image = getImageOrDownload(imgApi(imageUrls[0], 640, 147))

                drawImageRRect(image, rrr)
            }

            drawRRect(RRect.makeXYWH(663f, 30f, 65f, 32f, 5f), Paint().apply {
                color = Color.makeRGB(251, 114, 153)
            })

            val f = Font(Typeface.makeFromName("Noto Sans SC", FontStyle.NORMAL), 25f)

            drawTextLine(TextLine.make("专栏", f), 673f, 54f, Paint().apply {
                color = Color.WHITE
            })

            val font = Font(Typeface.makeFromName("Noto Sans SC", FontStyle.NORMAL), 20f)

            val textY = writeText(title, 30f, imgHeight + 40f, contentWidth - 30, 1, font, Paint().apply {
                color = Color.BLACK
            })
            writeText(desc, 30f, textY + 25f, contentWidth - 30, 3, font.makeWithSize(16f), Paint().apply {
                color = Color.makeRGB(148, 147, 147)
            })

        }


    }.makeImageSnapshot()
}

private fun makeInfoContent(text: String): Image{
    return Surface.makeRasterN32Premul(contentWidth,30).apply {
        canvas.apply {
            val font = Font(Typeface.makeFromName("Noto Sans SC", FontStyle.NORMAL), 20f)
            drawTextLine(TextLine.make(text, font), 0f, 0f, Paint().apply {
                color = Color.WHITE
            })
        }
    }.makeImageSnapshot()
}

private fun Canvas.writeText(t: String, x: Float, y: Float, rowL: Int, rowCount: Int, font: Font, paint: Paint): Float {
    var rowLength = 0f
    var textX = x
    var textY = y
    var textRow = 1
    val text = t.replace("\n", " ")

    for (c in text) {
        val ct = TextLine.make(c.toString(), font)
        val l = ct.width
        rowLength += l
        if (rowLength >= rowL) {
            if (textRow == rowCount) {
                drawTextLine(TextLine.make("...", font), textX, textY, paint)
                break
            } else {
                drawTextLine(ct, textX, textY, paint)
            }
            rowLength = 0f
            textX = x
            textY += font.size + 3
            textRow++
        } else {
            drawTextLine(ct, textX, textY, paint)
            textX += l
        }
    }
    return textY
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

suspend fun getImageOrDownload(url: String) = Image.makeFromEncoded(getOrDownload(url))

suspend fun getOrDownload(url: String, block: HttpRequestBuilder.() -> Unit = {}): ByteArray {
    HttpClient(OkHttp).use { client ->
        client.get<ByteArray>(url, block).also { data ->
            return data
        }
    }
}