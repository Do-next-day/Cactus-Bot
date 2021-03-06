package icu.dnddl.plugin.genshin.draw

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Rect
import kotlin.experimental.ExperimentalTypeInference
import kotlin.math.ceil

/**
 * 构建一个 [horizontal] x [vertical] 的布局
 *
 * 如 3x3 得到9个box
 *
 * 每个box的绘制指定在[box]内完成
 *
 * @param area 指定建立布局的区域
 * @param padding
 * 指定内边距
 *
 * 大小为1x1时不生效
 * @param horizontal 横向
 * @param vertical 纵向
 */
class AverLayer internal constructor(
    val area: Rect,
    val padding: Float,
    val horizontal: Int,
    val vertical: Int,
) {
    val size get() = horizontal * vertical
    val boxWidth: Float
        get() {
            if (horizontal == 1) return area.width
            return (area.width - (padding * (horizontal - 1))) / horizontal
        }
    val boxHeight: Float
        get() {
            if (vertical == 1) return area.height
            return (area.height - (padding * (vertical - 1))) / vertical
        }

    fun Canvas.box(position: Int = 1, block: Canvas.() -> Unit) {
        require(position in 1..size) { "Position position must be in 1 to $size, but is $position" }
        val count = save()
        translate(
            area.left + (boxWidth + padding) * ((position - 1) % horizontal),
            area.top + (boxHeight + padding) * (ceil((position.toFloat() / horizontal)) - 1)
        )
        block()
        restoreToCount(count)
    }

    init {
        require(horizontal > 0 && vertical > 0)
    }
}

/**
 * @see AverLayer
 */
@OptIn(ExperimentalTypeInference::class)
fun averLayer(
    area: Rect,
    padding: Float = 0f,
    horizontal: Int = 1,
    vertical: Int = 1,
    @BuilderInference block: AverLayer.() -> Unit
): AverLayer {
    return AverLayer(area, padding, horizontal, vertical).apply(block)
}