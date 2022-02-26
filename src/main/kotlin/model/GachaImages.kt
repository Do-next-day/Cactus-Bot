package org.laolittle.plugin.genshin.model

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect
import org.laolittle.plugin.Fonts
import org.laolittle.plugin.genshin.database.Character
import org.laolittle.plugin.genshin.util.SkikoImage
import org.laolittle.plugin.genshin.util.gachaDataFolder


object GachaImages {
    val GENSHIN_FONT = Fonts["GenshinSans-Bold"]

    private val BG_FOLDER = gachaDataFolder.resolve("background").also { it.mkdirs() }
    private val ATLAS_FOLDER = gachaDataFolder.resolve("atlas").also { it.mkdirs() }
    private val CHARACTER_FOLDER = gachaDataFolder.resolve("characters").also { it.mkdirs() }

    // width: 121 height: 529
    val GACHA_BACKDROP = Rect.makeLTRB(544F, 245F, 665F, 774F)

    val GACHA_PURE_BG = Rect.makeLTRB(385F, 503F, 539F, 1020F)

    val STARRY = Rect.makeLTRB(2F, 102F, 213F, 1021F)


    val SETTLEMENT_BACKGROUND by lazy {
        BG_FOLDER.resolve("settlement_bg2.png").SkikoImage
    }

    val GACHA_ATLAS_SPRITE by lazy {
        ATLAS_FOLDER.resolve("gacha_sprite.png").SkikoImage
    }

    val Character.gachaImage get() = CHARACTER_FOLDER.resolve("gacha_${id.value}.png").SkikoImage

    fun Canvas.drawBackDrop(dst: Rect, paint: Paint? = null) =
        drawImageRect(
            GACHA_ATLAS_SPRITE,
            GACHA_BACKDROP,
            dst,
            paint
        )
}