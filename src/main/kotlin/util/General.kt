package org.laolittle.plugin.genshin.util

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.jetbrains.skia.Image
import org.laolittle.plugin.genshin.CactusBot
import org.laolittle.plugin.genshin.database.Avatar
import org.laolittle.plugin.genshin.database.Equip
import org.laolittle.plugin.genshin.database.GachaItem
import java.io.File
import java.util.*

private val dataFolder get() = CactusBot.dataFolder

val gachaDataFolder = dataFolder.resolve("GachaImages")

val characterDataFolder = dataFolder.resolve("Characters")

val File.skikoImage: Image get() = Image.makeFromEncoded(readBytes())

inline fun <reified R> Json.decodeFromStringOrNull(str: String) = kotlin.runCatching {
    decodeFromString<R>(serializersModule.serializer(), str)
}.getOrNull()

fun <T> Json.decodeFromStringOrNull(deserializer: DeserializationStrategy<T>, string: String): T? =
    kotlin.runCatching {
        decodeFromString(deserializer, string)
    }.getOrNull()

val Json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    allowStructuredMapKeys = true
}

val randomUUID get() = UUID.randomUUID().toString().replace("-", "").lowercase()

fun Iterable<GachaItem>.sort(): MutableList<GachaItem> {
    val sorted = mutableListOf<GachaItem>()

    asSequence().filterNot { item ->
        (item is Avatar && item.star).also {
            if (it) sorted.add(item)
        }
    }.filterNot { item ->
        (item is Equip && item.star == 5).also {
            if (it) sorted.add(item)
        }
    }.filterNot { item ->
        (item is Avatar).also {
            if (it) sorted.add(item)
        }
    }.filterNot { item ->
        (item is Equip && item.star == 4).also {
            if (it) sorted.add(item)
        }
    }.filterIsInstance<Equip>().onEach(sorted::add).toList()

    return sorted
}