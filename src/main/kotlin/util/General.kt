package org.laolittle.plugin.genshin.util

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.jetbrains.skia.Image
import org.laolittle.plugin.genshin.GenshinHelper
import java.io.File

private val dataFolder get() = GenshinHelper.dataFolder

val gachaDataFolder = dataFolder.resolve("GachaImages")

val characterDataFolder = dataFolder.resolve("Characters")

val File.SkikoImage: Image get() = Image.makeFromEncoded(readBytes())

inline fun <reified R> Json.decodeFromStringOrNull(str: String) =
    kotlin.runCatching {
        decodeFromString<R>(serializersModule.serializer(), str)
    }.getOrNull()

internal val Json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    allowStructuredMapKeys = true
}