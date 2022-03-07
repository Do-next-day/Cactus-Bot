package icu.dnddl.plugin.genshin.draw

import icu.dnddl.plugin.genshin.CactusBot
import org.jetbrains.skia.*

internal fun getImageFromResource(name: String) =
    Image.makeFromEncoded(CactusBot::class.java.getResource(name)!!.openStream().use { it.readBytes() })

internal fun Canvas.drawImageRectNearest(image: Image, src: Rect, dst: Rect) = drawImageRect(image, src, dst,FilterMipmap(FilterMode.LINEAR, MipmapMode.NEAREST),null, true)

internal fun Canvas.drawImageRectNearest(image: Image, dst: Rect) = drawImageRectNearest(image, Rect(0f, 0f, image.width.toFloat(), image.height.toFloat()), dst)