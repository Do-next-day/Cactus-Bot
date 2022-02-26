package org.laolittle.plugin.genshin.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.jetbrains.skia.Image
import org.laolittle.plugin.genshin.GenshinHelper
import java.io.File

private val dataFolder get() = GenshinHelper.dataFolder

fun <T> runPlugin(block: suspend CoroutineScope.() -> T): T =
    runBlocking(GenshinHelper.coroutineContext, block)

val gachaDataFolder = dataFolder.resolve("GachaImages")

val characterDataFolder = dataFolder.resolve("Characters")

val File.SkikoImage: Image get() = Image.makeFromEncoded(readBytes())