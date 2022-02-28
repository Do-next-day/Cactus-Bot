package org.laolittle.plugin.genshin.model

import org.laolittle.plugin.genshin.util.gachaDataFolder
import org.laolittle.plugin.genshin.util.skikoImage

object Tenti {
    /**
     * Width: 235
     *
     * Height: (int) 235 * 4.37 = 1026
     * */

    private val BORDER_FOLDER = gachaDataFolder.resolve("border").also { it.mkdirs() }


    val GOLD by lazy { BORDER_FOLDER.resolve("gold.png").skikoImage }


    val PURPLE by lazy { BORDER_FOLDER.resolve("purple.png").skikoImage }


    val BLUE by lazy { BORDER_FOLDER.resolve("blue.png").skikoImage }


}