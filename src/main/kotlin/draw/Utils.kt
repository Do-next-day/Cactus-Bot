package icu.dnddl.plugin.genshin.draw

import icu.dnddl.plugin.genshin.CactusBot
import org.jetbrains.skia.Image

fun getImageFromResource(name: String) =
    Image.makeFromEncoded(CactusBot::class.java.getResource(name)!!.openStream().use { it.readBytes() })