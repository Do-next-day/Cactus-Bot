package org.laolittle.plugin.util

import org.laolittle.plugin.database.Character
import java.awt.RenderingHints
import java.io.ByteArrayOutputStream
import java.io.File
import javax.imageio.ImageIO

object GachaRenderer {
    private const val BASE_PIXEL = 100
    fun renderGachaResult(characters: List<Character>): ByteArray {
        // 200px , 300px 100px/per
        val gacha = ImageIO.read(File("BG"))
        val g2 = gacha.createGraphics()
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_ANTIALIAS_ON)
        for ((i, perChar) in characters.withIndex()) {
            val character = ImageIO.read(File("${perChar.id}.jpg"))
            g2.drawImage(character, 150 + (BASE_PIXEL * (i + 1)), 85, null)
        }
        g2.dispose()
        val output = ByteArrayOutputStream()
        ImageIO.write(gacha, "png", output)
        output.use { return it.toByteArray() }
    }
}