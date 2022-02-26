package org.laolittle.plugin.genshin.model

import org.jetbrains.skia.*
import org.laolittle.plugin.genshin.database.Character
import org.laolittle.plugin.genshin.model.GachaImages.GACHA_ATLAS_SPRITE
import org.laolittle.plugin.genshin.model.GachaImages.GACHA_BACKDROP
import org.laolittle.plugin.genshin.model.GachaImages.GACHA_PURE_BG
import org.laolittle.plugin.genshin.model.GachaImages.STARRY
import org.laolittle.plugin.genshin.model.GachaImages.gachaImage
import org.laolittle.plugin.genshin.util.SkikoImage
import org.laolittle.plugin.genshin.util.gachaDataFolder

object Tenti {
    /**
     * Width: 235
     *
     * Height: (int) 235 * 4.37 = 1026
     * */
    val Character.card: Image
        get() {
            val character = gachaImage

            val w = 235
            val h = (w * 4.37).toInt()

            return Surface.makeRasterN32Premul(w, h).apply {
                val dst = Rect.makeWH(w.toFloat(), h.toFloat())
                val paint = Paint().apply {
                    blendMode = BlendMode.SRC_ATOP
                }
                canvas.apply {
                    drawImageRect(
                        GACHA_ATLAS_SPRITE,
                        GACHA_PURE_BG,
                        dst
                    )

                    drawImageRect(
                        GACHA_ATLAS_SPRITE,
                        GACHA_BACKDROP,
                        dst,
                        paint
                    )

                    drawImage(character, -40f, 17F, paint)

                    drawImageRect(GACHA_ATLAS_SPRITE, STARRY, dst, paint.apply {
                        colorFilter = ColorFilter.makeMatrix(
                            ColorMatrix(
                                3F, 0F, 0F, 0F, 0F,
                                0F, 3F, 0F, 0F, 0F,
                                0F, 0F, 3F, 0F, 0F,
                                0F, 0F, 0F, 0.15F, 0F,
                            )
                        )
                    })
                }
            }.makeImageSnapshot()
        }

    private val BORDER_FOLDER = gachaDataFolder.resolve("border").also { it.mkdirs() }


    val GOLD by lazy {
        BORDER_FOLDER.resolve("gold.png").SkikoImage
    }

    val PURPLE by lazy {
        BORDER_FOLDER.resolve("purple.png").SkikoImage
    }

    val BLUE by lazy {
        BORDER_FOLDER.resolve("blue.png").SkikoImage
    }
}