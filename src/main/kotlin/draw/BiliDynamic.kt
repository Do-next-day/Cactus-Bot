package icu.dnddl.plugin.genshin.draw

import icu.dnddl.plugin.genshin.CactusConfig.image
import icu.dnddl.plugin.genshin.api.bilibili.data.*
import org.jetbrains.skia.*
import org.laolittle.plugin.getBytes
import java.awt.SystemColor.text
import java.io.File

private const val cardWidth = 800
private const val margin = 50

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

fun makeDynamicImage(){
    Surface.makeRasterN32Premul(cardWidth, cardWidth).apply {
        canvas.apply {
            drawDynamicBg(Rect(0f,0f, cardWidth.toFloat(), cardWidth.toFloat()))
//            val f = Font(Typeface.makeFromName("Noto Sans SC", FontStyle.NORMAL), 35f)
//            val t = TextLine.make("cccccccccc", f)

            drawImage(makeTextContentImage("AAAAA"), 0f,0f,null)

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
        alpha = 50
    })
    drawRectShadowNoclip(rr,10f,10f,30f,10f,Color.makeARGB(85, 0,0,0))

    /**
     * 白色
     */
//    drawRRect(rr, Paint().apply {
//        color = Color.WHITE
//        alpha = 90
//    })
//    drawRectShadow(rr,10f,10f,30f,10f,Color.makeARGB(50, 0,0,0))

}

private fun makeTextContentImage(text: String): Image {

    return Surface.makeRasterN32Premul(cardWidth - margin * 2 - 20, 100).apply {
        canvas.apply {
            val f = Font(Typeface.makeFromName("Noto Sans SC", FontStyle.NORMAL), 35f)
            val t = TextLine.make(text, f)
            drawTextLine(t, 0f, 0f, Paint())
        }
    }.makeImageSnapshot()

}