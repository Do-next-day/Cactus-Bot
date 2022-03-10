package icu.dnddl.plugin.genshin.draw

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Font
import org.jetbrains.skia.Paint
import org.jetbrains.skia.TextLine

private typealias Color = Int
class TextInfo(private val list: MutableList<Pair<TextLine, Color>>): MutableList<Pair<TextLine, Color>> by list{
    constructor() : this(mutableListOf())

    fun String.use(font: Font) = TextLine.make(this, font)

    fun TextLine.with(color: Color) = add(this to color)
}

fun makeText(block: TextInfo.() -> Unit): TextInfo {
    return TextInfo().apply(block)
}

fun Canvas.drawTextWithInfo(text: TextInfo, x: Float, y: Float) {
    text.forEach { (line, color) ->
        drawTextLine(line, x, y, Paint().apply {
            this.color = color
        })
    }
}