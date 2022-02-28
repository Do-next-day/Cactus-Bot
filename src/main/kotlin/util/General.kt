package org.laolittle.plugin.genshin.util

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.jetbrains.skia.Image
import org.laolittle.plugin.genshin.CactusBot
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

val randomUUID get() = UUID.randomUUID().toString().replace("-", "").uppercase()

@Serializable
data class UserCookie(
    val cookies: String? = null,
    val uuid: String = randomUUID,
)