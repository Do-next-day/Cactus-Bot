package icu.dnddl.plugin.genshin.draw

import icu.dnddl.plugin.genshin.CactusBot
import org.jetbrains.skia.*

internal fun getImageFromResource(name: String) =
    Image.makeFromEncoded(CactusBot::class.java.getResource(name)!!.openStream().use { it.readBytes() })

/**
 * 同[Canvas.drawImageRect], 使用`双线性插值`和`最邻近过滤`进行图像绘制
 *
 * @see Canvas.drawImageRect
 */
internal fun Canvas.drawImageRectNearest(image: Image, src: Rect, dst: Rect, paint: Paint? = null) =
    drawImageRect(image, src, dst, FilterMipmap(FilterMode.LINEAR, MipmapMode.NEAREST), paint, true)

/**
 * @see drawImageRectNearest
 */
internal fun Canvas.drawImageRectNearest(image: Image, dst: Rect, paint: Paint? = null) =
    drawImageRectNearest(image, Rect(0f, 0f, image.width.toFloat(), image.height.toFloat()), dst, paint)

/**
 * 从目标源截取[Rect]并绘制到指定坐标
 */
internal fun Canvas.drawImageRectTo(image: Image, src: Rect, x: Float, y: Float) =
    drawImageRect(image, src, Rect.makeXYWH(x, y, src.width, src.height))

internal fun Image.zoomLeftAtPoint(
    verticalTopPoint: Float,
    verticalBottomPoint: Float,
    dstWidth: Int,
    dstHeight: Int,
    dstPadding: Rect = Rect(0f, 0f, 0f, 0f),
    srcPadding: Rect = Rect(0f, 0f, 0f, 0f)
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

internal fun Image.zoomRightAtPoint(
    verticalTopPoint: Float,
    verticalBottomPoint: Float,
    dstWidth: Int,
    dstHeight: Int,
    dstPadding: Rect = Rect(0f, 0f, 0f, 0f),
    srcPadding: Rect = Rect(0f, 0f, 0f, 0f)
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

internal fun Image.zoomTopAtPoint(
    horizontalLeftPoint: Float,
    horizontalRightPoint: Float,
    dstWidth: Int,
    dstHeight: Int,
    dstPadding: Rect = Rect(0f, 0f, 0f, 0f),
    srcPadding: Rect = Rect(0f, 0f, 0f, 0f)
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

internal fun Image.zoomVerticalAtPoint(
    verticalTopPoint: Float,
    verticalBottomPoint: Float,
    dstHeight: Int,
    dstPadding: Rect = Rect(0f, 0f, 0f, 0f),
    srcPadding: Rect = Rect(0f, 0f, 0f, 0f)
) = zoomAroundAtPoint(
    this.height.toFloat(),
    this.height.toFloat(),
    verticalTopPoint,
    verticalBottomPoint,
    this.width,
    dstHeight,
    dstPadding,
    srcPadding
)

internal fun Image.zoomHorizontalAtPoint(
    horizontalLeftPoint: Float,
    horizontalRightPoint: Float,
    dstWidth: Int,
    dstPadding: Rect = Rect(0f, 0f, 0f, 0f),
    srcPadding: Rect = Rect(0f, 0f, 0f, 0f)
) = zoomAroundAtPoint(
    horizontalLeftPoint,
    horizontalRightPoint,
    this.width.toFloat(),
    this.width.toFloat(),
    dstWidth,
    this.height,
    dstPadding,
    srcPadding
)

internal fun Image.zoomAroundAtCornerWidth(
    cornerWidth: Float,
    dstWidth: Int,
    dstHeight: Int,
    dstPadding: Rect = Rect(0f, 0f, 0f, 0f),
    srcPadding: Rect = Rect(0f, 0f, 0f, 0f)
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

internal fun Image.zoomAroundAtPoint(
    horizontalLeftPoint: Float,
    horizontalRightPoint: Float,
    verticalTopPoint: Float,
    verticalBottomPoint: Float,
    dstWidth: Int,
    dstHeight: Int,
    dstPadding: Rect = Rect(0f, 0f, 0f, 0f),
    srcPadding: Rect = Rect(0f, 0f, 0f, 0f)
): Surface = zoomAroundAtRect(
    Rect(srcPadding.left, srcPadding.top, horizontalLeftPoint, verticalTopPoint),
    Rect(horizontalRightPoint, srcPadding.top, this.width.toFloat() - srcPadding.right, verticalTopPoint),
    Rect(
        horizontalRightPoint,
        verticalBottomPoint,
        this.width.toFloat() - srcPadding.right,
        this.height.toFloat() - srcPadding.bottom
    ),
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
internal fun Image.zoomAroundAtRect(
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
    padding: Rect = Rect(0f, 0f, 0f, 0f),
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

/**
 * 将图片从目标区域[Rect]绘制到指定区域[Rect]
 * 图片按比例缩放, 不会修改比例
 * 过高的区域会被忽略
 *
 * @param src 目标源区域
 * @param dst 指定绘制区域
 */
internal fun Canvas.drawImageClipHeight(image: Image, src: Rect, dst: Rect, paint: Paint? = null) {
    val foo = image.height.toFloat() / image.width
    Surface.makeRasterN32Premul(dst.width.toInt(), dst.height.toInt()).apply {
        canvas.apply {
            drawImageRect(image, src, Rect.makeWH(dst.width, dst.width * foo))
        }
    }.draw(this, dst.left.toInt(), dst.top.toInt(), paint)
}


/**
 * 将图片绘制到指定区域[Rect]
 *
 * @see drawImageClipHeight
 */
internal fun Canvas.drawImageClipHeight(image: Image, dst: Rect, paint: Paint? = null) =
    drawImageClipHeight(image, Rect.makeWH(image.width.toFloat(), image.height.toFloat()), dst, paint)


/**
 * 图片内容位置
 * TOP 居上
 * CENTER 居中
 * BOTTOM 居下
 */
enum class ImagePosition {
    TOP,
    CENTER,
    BOTTOM
}

/**
 * 等比缩放图片并按裁剪高出的部分
 *
 * @param dstWidth 目标宽度
 * @param dstHeight 目标高度
 * @param contentPosition 图片内容位置
 * @param dstPosition 图片绘制位置
 */
internal fun Canvas.drawImageClipHeight(
    image: Image,
    dstWidth: Int,
    dstHeight: Int,
    contentPosition: ImagePosition,
    dstPosition: Point
){
    val imgDstHeight = image.width * dstHeight / dstWidth

    val offsetY = when (contentPosition){
        ImagePosition.TOP -> 0
        ImagePosition.CENTER -> (image.height - imgDstHeight) / 2
        ImagePosition.BOTTOM -> image.height - imgDstHeight
    }

    drawImageRectNearest(
        image,
        Rect.makeXYWH(0f, offsetY.toFloat(), image.width.toFloat(), imgDstHeight.toFloat()),
        Rect.makeXYWH(dstPosition.x, dstPosition.y, dstWidth.toFloat(), dstHeight.toFloat())
    )
}

/**
 * 按比例缩放图片并裁剪高出的部分
 *
 * @param proportion 比例 如: 16/6
 * @param dstWidth 目标宽度
 * @param contentPosition 图片内容位置
 * @param dstPosition 图片绘制位置
 */
internal fun Canvas.drawImageAtProportion(
    image: Image,
    proportion: String,
    dstWidth: Int,
    contentPosition: ImagePosition,
    dstPosition: Point
){
    val split = proportion.split("/").map { s -> s.toInt() }
    drawImageClipHeight(
        image,
        dstWidth,
        dstWidth * split[1] / split[0],
        contentPosition,
        dstPosition
    )
}
