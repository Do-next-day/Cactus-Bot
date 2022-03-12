package icu.dnddl.plugin.genshin.draw

import com.vdurmont.emoji.EmojiParser
import icu.dnddl.plugin.genshin.api.bilibili.data.*
import icu.dnddl.plugin.genshin.service.PluginDispatcher
import icu.dnddl.plugin.genshin.util.decode
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import org.jetbrains.skia.*
import org.laolittle.plugin.getBytes
import java.io.File


// 动态图片宽度
private const val cardWidth = 840
// 动态卡片外边距
private const val cardMargin = 40
// 内容外边距
private const val contentMargin = 60
// 内容宽度
private const val contentWidth = cardWidth - contentMargin * 2


suspend fun String.dynamicImage(type: Int, dynamicInfo: DynamicInfo): List<Image> {
    val font = Font(Typeface.makeFromName("Noto Sans SC", FontStyle.NORMAL), 25f)

    return when (type) {
//        DynamicType.REPLY -> {
//            decode<DynamicReply>()
//        }
        DynamicType.PICTURE -> {
            with(decode<DynamicPicture>()){
                listOf(
                    makeTextContent(detail.description,dynamicInfo.display.emojiInfo?.emojiDetails, font,
                        Paint().apply { color = Color.WHITE }
                    ),
                    makeImageContent(detail.pictures)
                )
            }
        }
        DynamicType.TEXT -> {
            listOf(
                makeTextContent(decode<DynamicText>().detail.content, dynamicInfo.display.emojiInfo?.emojiDetails, font,
                    Paint().apply { color = Color.WHITE }
                )
            )
        }
        DynamicType.VIDEO -> {
            with(decode<DynamicVideo>()){
                listOf(
                    makeTextContent(dynamic, dynamicInfo.display.emojiInfo?.emojiDetails, font, Paint().apply {
                        color = Color.WHITE
                    }),
                    makeVideoContent(cover, title, description, font.makeWithSize(20f), Paint().apply {
                        color = Color.WHITE
                    })
                )
            }
        }
        DynamicType.ARTICLE -> {
            with(decode<DynamicArticle>()){
                listOf(
                    makeArticleContent(images, title, summary, font.makeWithSize(20f), Paint().apply {
                        color = Color.WHITE
                    })
                )
            }

        }
//        DynamicType.MUSIC -> decode<DynamicMusic>()
//        DynamicType.EPISODE -> decode<DynamicEpisode>()
//        DynamicType.SKETCH -> decode<DynamicSketch>()
//        DynamicType.LIVE, DynamicType.LIVE_ING -> decode<DynamicLive>()
        else -> {
            listOf(
                makeInfoContent("不支持此类型动态 type:${type}", font, Paint().apply {
                    color = Color.WHITE
                })
            )
        }
    }
}

suspend fun makeDynamicImage(){

    Surface.makeRasterN32Premul(cardWidth, cardWidth).apply surface@{
        canvas.apply {
            drawDynamicBg(RRect.makeXYWH(0f,0f, this@surface.width.toFloat(), this@surface.height.toFloat(), 10f))

            // Noto Sans SC  HarmonyOS Sans SC
            val font = Font(Typeface.makeFromName("Noto Sans SC", FontStyle.NORMAL), 25f)
            val paint = Paint().apply {
                color = Color.WHITE
                isAntiAlias = true
            }

            //https://i1.hdslb.com/bfs/face/d3587e6f3b534499fc08a71296bafa74a159fa33.pn
            drawImage(makeHeader("猫芒ベル_Official", "2022-03-12 17:40",
                "https://i1.hdslb.com/bfs/face/1c202e4750bceb1692b60f5a0d6a004a2013d242.jpg",
                "https://i1.hdslb.com/bfs/face/d3587e6f3b534499fc08a71296bafa74a159fa33.png",
                0,font.makeWithSize(30f), paint.apply {
                    color = Color.makeRGB(251, 114, 153)
                }), contentMargin.toFloat(), cardMargin.toFloat(),null)

            val tt = "[原神_哼]下午3点半！开始[原神_欸嘿]开始开始AAAAAAAAAAAAAAAAAAAAAAAAAAAA始开始原神原神原神原神原神\n" +
                    "原神~！！！！\n" +
                    "新活动来了！！！[原神_嗯]\n" +
                    "我爱可莉！！！！！！！！！"
            drawImage(makeTextContent(tt, null, font, Paint().apply {
                color = Color.WHITE
                isAntiAlias = true
            }), contentMargin.toFloat(), contentMargin.toFloat() + 100,null)

            val images = listOf(
                    DynamicPictureInfo(1080,null,"https://i0.hdslb.com/bfs/album/648fb2527f49fd42c6ecbf991151e1593e7225ad.jpg",1920),
//                    DynamicPictureInfo(816,null,"https://i0.hdslb.com/bfs/album/0ae6fee9eaeafe614377cf5451e78c2430d5a6e4.gif",499),
//                    DynamicPictureInfo(816,null,"https://i0.hdslb.com/bfs/album/56c135d05c0d4a77964db04a07f039d7fe945f14.gif",499),
//                    DynamicPictureInfo(816,null,"https://i0.hdslb.com/bfs/album/1971358d4d71ded8c1b287c7377a32c397190490.gif",499),
//                    DynamicPictureInfo(816,null,"https://i0.hdslb.com/bfs/album/554027b1f38ad88040e315cdf55ee98ed5a20335.gif",499),
//                    DynamicPictureInfo(816,null,"https://i0.hdslb.com/bfs/album/2180d689ad3de225f70e37f6ae3e953f583b012a.gif",499),
//                    DynamicPictureInfo(816,null,"https://i0.hdslb.com/bfs/album/d009f31357f049f7f8134e4e74e208db4284c2eb.gif",499),
                )

//            drawImage(makeImageContent(images), contentMargin.toFloat(), 200f ,null)
//
//
//            drawImage(makeVideoContent("https://i0.hdslb.com/bfs/archive/5c20fc634ca754acc6b48a445a2980b9a4942107.jpg",
//                "趣味视频征集活动今日",
//                "视频内容不限，包括但不限于打法攻略、创意玩法等内容视频内容不限，包括但不限于打法攻略、创意玩法等内容",
//                font.makeWithSize(20f),
//                paint.apply { color = Color.BLACK }
//            ), contentMargin.toFloat(), 650f ,null)
//
//            drawImage(makeArticleContent(listOf("https://i0.hdslb.com/bfs/archive/5c20fc634ca754acc6b48a445a2980b9a4942107.jpg"),
//                "趣味视频征集活动今日趣味视频征集活动今日趣味视频征集活动今日趣味视频征集活动今日趣味视频征集活动今日",
//                "视频内容不限，包括但不限于打法攻略、创意玩法等内容视频内容不限，包括但不限于打法攻略、创意玩法等内容",
//                font.makeWithSize(20f),
//                paint.apply { color = Color.BLACK }),
//                contentMargin.toFloat(), 1150f ,null)

//            drawImage(makeInfoContent("测试测试", font, paint), contentMargin.toFloat(),200f ,null)


        }
        File("dynamic.png").writeBytes(makeImageSnapshot().getBytes())
    }
}

private fun Canvas.drawDynamicBg(rect: RRect){
//    drawRect(rect, Paint().apply {
//        shader = Shader.makeLinearGradient(
//            Point(rect.left,rect.top),
//            Point(rect.right,rect.bottom),
//            intArrayOf(
//                0xFFD16BA5.toInt(), 0xFFC777B9.toInt(), 0xFFBA83CA.toInt(), 0xFFAA8FD8.toInt(),
//                0xFF9A9AE1.toInt(), 0xFF8AA7EC.toInt(), 0xFF79B3F4.toInt(), 0xFF69BFF8.toInt(),
//                0xFF52CFFE.toInt(), 0xFF41DFFF.toInt(), 0xFF46EEFA.toInt(), 0xFF5FFBF1.toInt()
//            )
//        )
//    })

    val bgImg = getImageFromResource("/GenshinRecord/UI_LanternRite_EntrustPage_Bg02.png")

    val ratio = bgImg.width.toFloat() / bgImg.height.toFloat()

    val srcRect = if (rect.width / ratio < rect.height){
        val imgW = rect.width * bgImg.height / rect.height
        val offsetX = (bgImg.width - imgW) / 2
        println("X")
        Rect.makeXYWH(offsetX, 0f, imgW, bgImg.height.toFloat())
    }else{
        val imgH = rect.height * bgImg.width / rect.width
        val offsetY = (bgImg.height - imgH) / 2
        println("Y")
        Rect.makeXYWH(0f, offsetY, bgImg.width.toFloat(), imgH)
    }

    drawImageRect(bgImg, srcRect, rect, null)

    val rr = RRect.makeComplexLTRB(
        rect.left + cardMargin,
        rect.top + cardMargin,
        rect.right - cardMargin,
        rect.bottom - cardMargin,
        rect.radii
    )

    drawRRect(rr, Paint().apply {
            imageFilter = ImageFilter.makeBlur(15f,15f,
                FilterTileMode.CLAMP,
                ImageFilter.makeImage(
                    bgImg,
                    srcRect.inflate(cardMargin * -1f),
                    rect.inflate(cardMargin * -1f),
                    SamplingMode.MITCHELL
                )
            )
        }
    )

    /**
     * 黑色
     */
    drawRRect(rr, Paint().apply {
        color = Color.BLACK
        alpha = 90
    })
    drawRectShadowAntiAlias(rr.inflate(1f),8f,8f,25f,0f,Color.makeARGB(80, 0,0,0))

    /**
     * 白色
     */
//    drawRRect(rr, Paint().apply {
//        color = Color.WHITE
//        alpha = 90
//    })
//    drawRectShadowAntiAlias(rr,10f,10f,30f,10f,Color.makeARGB(50, 0,0,0))
}

private suspend fun makeHeader(
    rowOne: String,
    rowTwo: String,
    faceUrl: String,
    pendentUrl: String = "",
    offset: Int = 0,
    font: Font,
    paint: Paint,
): Image {
    return Surface.makeRasterN32Premul(cardWidth, 100).apply {
        canvas.apply {
//            drawRect(Rect.Companion.makeWH(cardWidth.toFloat(), 100f),Paint().apply {
//                color = Color.BLACK
//            })
            val radius = 60f
            drawCircle(radius, radius, radius/2, Paint().apply {
                color = Color.makeRGB(210, 160, 120)
                isAntiAlias = true
            })
            val avatar = getImageOrDownload(faceUrl)

            drawImageRectNearest(avatar, Rect.makeXYWH(radius/2, radius/2, radius, radius), Paint().apply {
                blendMode = BlendMode.SRC_ATOP
            })

            if (pendentUrl.isNotEmpty()){
                val pendent = getImageOrDownload(pendentUrl)

                drawImageRectNearest(pendent, Rect(10f, 10f, radius + 50, radius + 50), Paint())
            }

            writeText(rowOne, 110f + offset, 60f, 600, 1, font, paint)

            writeText(rowTwo, 110f + offset, 90f, 600, 1,
                font.makeWithSize(font.size - 10),
                paint.apply {
                    color = Color.WHITE
                    alpha = 170
                }
            )

        }
    }.makeImageSnapshot()
}

private suspend fun makeTextContent(text: String, emojiList: List<EmojiDetails>? = null, font: Font, paint: Paint, ): Image {

    var msgText = text
    val lineHeight = font.size * 1.3f
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

                    PluginDispatcher.runCatching {
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
                    if (textX + t.width > contentWidth) {
                        textX = 0f
                        textY += lineHeight
                    }
                    if (c.toString().matches("[\u4e00-\u9fa5]".toRegex())) {
                        drawTextLine(t, textX+1, textY, paint)
                        textX+=2
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
    val picArc = 20
    val picPadding = 10
    var picX = 0

    val surface = Surface.makeRasterN32Premul(contentWidth + 10, 1000).apply {
        canvas.apply {

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

                drawRectShadowAntiAlias(rr, 2f, 2f, 6f, 0f, Color.makeARGB(130,0,0,0))

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

private suspend fun makeVideoContent(coverUrl: String, title: String, desc: String, font: Font, paint: Paint): Image {

    val picArc = 10
    val imgHeight = 360
    val cardHeight = 470

    return Surface.makeRasterN32Premul(contentWidth + 10, cardHeight + 10).apply {
        canvas.apply {

            val rr = RRect.makeXYWH(0f, 0f, contentWidth.toFloat(), cardHeight.toFloat(), picArc.toFloat())

            drawRRect(rr, Paint().apply {
                color = Color.WHITE
                alpha = 160
            })

            val image = getImageOrDownload(imgApi(coverUrl, contentWidth, imgHeight))

            val offsetY = (image.height - imgHeight) / 2
            drawImageRRect(
                image,
                Rect.makeXYWH(0f, offsetY.toFloat(), image.width.toFloat(), imgHeight.toFloat()),
                RRect.makeXYWH(0f, 0f, contentWidth.toFloat(), imgHeight.toFloat(), picArc.toFloat())
            )


            val tagX = 630f
            val tagY = 20f

            drawRRect(RRect.makeXYWH(tagX, tagY, 69f, 35f, 5f), Paint().apply {
                color = Color.makeRGB(251, 114, 153)
            })

            drawTextLine(TextLine.make("视频", font.makeWithSize(25f)), tagX+9, tagY+25, Paint().apply {
                color = Color.WHITE
            })


            val textY = writeText(title, 10f, imgHeight + 30f, contentWidth - 35, 1, font, paint)
            writeText(desc, 10f, textY + 25f, contentWidth - 35, 3, font.makeWithSize(font.size * 0.8f),
                paint.apply {
                    alpha = 170
                }
            )

            drawRectShadowAntiAlias(rr,3f,3f,6f,0f,Color.makeARGB(100, 0,0,0))

        }
    }.makeImageSnapshot()
}

private suspend fun makeArticleContent(imageUrls: List<String>, title: String, desc: String, font: Font, paint: Paint): Image{

    val picArc = 10
    val imgHeight = 170
    val cardHeight = 275

    return Surface.makeRasterN32Premul(contentWidth + 10, cardHeight + 10).apply {
        canvas.apply {

            val rr = RRect.makeXYWH(0f,0f, contentWidth.toFloat(), cardHeight.toFloat(), picArc.toFloat())
            drawRRect(rr, Paint().apply {
                color = Color.WHITE
                alpha = 180
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

            val tagX = 630f
            val tagY = 20f

            drawRRect(RRect.makeXYWH(tagX, tagY, 60f, 30f, 5f), Paint().apply {
                color = Color.makeRGB(251, 114, 153)
            })

            drawTextLine(TextLine.make("专栏", font.makeWithSize(20f)), tagX+9, tagY+22, Paint().apply {
                color = Color.WHITE
            })

            val textY = writeText(title, 10f, imgHeight + 30f, contentWidth - 35, 1, font, paint)
            writeText(desc, 10f, textY + 25f, contentWidth - 35, 3, font.makeWithSize(font.size * 0.8f),
                paint.apply {
                    alpha = 170
                }
            )

            drawRectShadowAntiAlias(rr,3f,3f,6f,0f,Color.makeARGB(100, 0,0,0))
        }
    }.makeImageSnapshot()
}

private fun makeInfoContent(text: String, font: Font, paint: Paint): Image{
    return Surface.makeRasterN32Premul(contentWidth,30).apply {
        canvas.apply {
            drawTextLine(TextLine.make(text, font), 0f, font.size, paint)
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