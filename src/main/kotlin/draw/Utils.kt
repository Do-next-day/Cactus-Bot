package icu.dnddl.plugin.genshin.draw

import icu.dnddl.plugin.genshin.CactusBot
import org.jetbrains.skia.*

internal fun getImageFromResource(name: String) =
    Image.makeFromEncoded(CactusBot::class.java.getResource(name)!!.openStream().use { it.readBytes() })

internal fun Canvas.drawImageRectNearest(image: Image, src: Rect, dst: Rect) = drawImageRect(image, src, dst,FilterMipmap(FilterMode.LINEAR, MipmapMode.NEAREST),null, true)

internal fun Canvas.drawImageRectNearest(image: Image, dst: Rect) = drawImageRectNearest(image, Rect(0f, 0f, image.width.toFloat(), image.height.toFloat()), dst)

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
