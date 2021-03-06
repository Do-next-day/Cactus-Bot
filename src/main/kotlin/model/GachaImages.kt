package icu.dnddl.plugin.genshin.model

import icu.dnddl.plugin.genshin.database.Avatar
import icu.dnddl.plugin.genshin.util.gachaDataFolder
import icu.dnddl.plugin.genshin.util.skikoImage
import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Paint
import org.jetbrains.skia.Rect
import org.laolittle.plugin.Fonts


object GachaImages {
    val GENSHIN_FONT = Fonts["GenshinSans-Bold"]

    private val BG_FOLDER = gachaDataFolder.resolve("background").also { it.mkdirs() }
    private val ATLAS_FOLDER = gachaDataFolder.resolve("atlas").also { it.mkdirs() }
    private val CHARACTER_FOLDER = gachaDataFolder.resolve("characters").also { it.mkdirs() }

    // width: 121 height: 529
    val GACHA_BACKDROP by lazy {
        Rect.makeLTRB(544F, 245F, 665F, 774F)
    }

    val GACHA_PURE_BG by lazy {
        Rect.makeLTRB(385F, 503F, 539F, 1020F)
    }

    val STARRY by lazy {
        Rect.makeLTRB(2F, 102F, 213F, 1021F)
    }

    val SETTLEMENT_BACKGROUND by lazy {
        BG_FOLDER.resolve("settlement_bg2.png").skikoImage
    }

    val GACHA_ATLAS_SPRITE by lazy {
        ATLAS_FOLDER.resolve("gacha_sprite.png").skikoImage
    }

    val Avatar.gachaImage get() = CHARACTER_FOLDER.resolve("gacha_${id.value}.png").skikoImage

    fun Canvas.drawBackDrop(dst: Rect, paint: Paint? = null) = drawImageRect(
        GACHA_ATLAS_SPRITE, GACHA_BACKDROP, dst, paint
    )
}