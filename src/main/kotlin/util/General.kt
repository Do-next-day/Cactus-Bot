package org.laolittle.plugin.genshin.util

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer
import net.mamoe.mirai.contact.Friend
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.message.data.PlainText
import org.jetbrains.skia.Image
import org.laolittle.plugin.genshin.CactusBot
import org.laolittle.plugin.genshin.CactusData
import org.laolittle.plugin.genshin.database.*
import java.io.File
import java.util.*
import net.mamoe.mirai.contact.User as MiraiUser

private val dataFolder get() = CactusBot.dataFolder

val gachaDataFolder = dataFolder.resolve("GachaImages")

val avatarDataFolder = dataFolder.resolve("Avatars")

val cacheFolder = dataFolder.resolve("cache")

val File.skikoImage: Image get() = Image.makeFromEncoded(readBytes())

inline fun <reified T> Json.decodeFromStringOrNull(str: String) =
    decodeFromStringOrNull<T>(serializersModule.serializer(), str)


fun <T> Json.decodeFromStringOrNull(deserializer: DeserializationStrategy<T>, string: String): T? =
    kotlin.runCatching {
        decodeFromString(deserializer, string)
    }.getOrNull()

inline fun <reified T> JsonElement.decode() = org.laolittle.plugin.genshin.util.Json.decodeFromJsonElement<T>(
    org.laolittle.plugin.genshin.util.Json.serializersModule.serializer(),
    this
)

val Json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    allowStructuredMapKeys = true
}

val randomUUID get() = UUID.randomUUID().toString().replace("-", "").lowercase()

fun Iterable<GachaItem>.sort(): List<GachaItem> {
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

suspend inline fun <reified T : MiraiUser> T.requireCookie(lazy: () -> Unit = {}): User {
    val userData = getUserData(this.id)
    if (userData.data.cookies.isBlank()) {
        val message = PlainText("请先加好友私聊发送”原神登录“进行登录")
        when (this) {
            is Friend -> sendMessage(message)
            is Member -> group.sendMessage(message)
        }
        lazy()
    }
    return userData
}

suspend fun getOrDownload(url: String, block: HttpRequestBuilder.() -> Unit = {}): ByteArray {
    val fileName = Regex("(.+)/(.+)$").find(url)?.groupValues?.last()
    val file = fileName?.let { cacheFolder.resolve(it) }
    return if (file?.isFile == true) file.readBytes()
    else {
        HttpClient(OkHttp).use { client ->
            client.get<ByteArray>(url, block).also { data ->
                file?.writeBytes(data)
            }
        }
    }
}

val currentTimeMillis get() = System.currentTimeMillis()

val userSettings by CactusData::userSetting

fun seconds(timeMillis: Long) = timeMillis * 1000

fun minutes(timeMillis: Long) = seconds(timeMillis) * 60