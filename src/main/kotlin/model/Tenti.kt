package org.laolittle.plugin.genshin.model

import org.laolittle.plugin.genshin.database.Avatar
import org.laolittle.plugin.genshin.database.Equip
import org.laolittle.plugin.genshin.database.GachaItem
import org.laolittle.plugin.genshin.util.gachaDataFolder
import org.laolittle.plugin.genshin.util.skikoImage

object Tenti {
    /**
     * Width: 235
     *
     * Height: (int) 235 * 4.37 = 1026
     * */

    private val BORDER_FOLDER = gachaDataFolder.resolve("border").also { it.mkdirs() }


    private val GOLD by lazy { BORDER_FOLDER.resolve("gold.png").skikoImage }


    private val PURPLE by lazy { BORDER_FOLDER.resolve("purple.png").skikoImage }


    private val BLUE by lazy { BORDER_FOLDER.resolve("blue.png").skikoImage }

    val GachaItem.border
        get() =
            when (this) {
                is Avatar -> if (star) GOLD else PURPLE
                is Equip ->
                    when (star) {
                        3 -> BLUE
                        4 -> PURPLE
                        else -> GOLD
                    }
                else -> BLUE
            }
}