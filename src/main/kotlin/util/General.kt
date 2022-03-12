package icu.dnddl.plugin.genshin.util

import icu.dnddl.plugin.genshin.CactusBot
import icu.dnddl.plugin.genshin.CactusData
import icu.dnddl.plugin.genshin.api.internal.SignResponse
import icu.dnddl.plugin.genshin.database.*
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
import java.io.File
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import net.mamoe.mirai.contact.User as MiraiUser

private val dataFolder get() = CactusBot.dataFolder

val gachaDataFolder = dataFolder.resolve("GachaImages").also { it.mkdir() }

val avatarDataFolder = dataFolder.resolve("Avatars").also { it.mkdir() }

val cacheFolder = dataFolder.resolve("cache").also { it.mkdir() }

val settingFile = dataFolder.resolve("userSettings.json").also { it.createNewFile() }

val awardsFile = cacheFolder.resolve("awards.json").also { it.createNewFile() }

val biliSubscribesFile = dataFolder.resolve("biliSubscribes.json").also { it.createNewFile() }

val File.skikoImage: Image get() = Image.makeFromEncoded(readBytes())

inline fun <reified T> Json.decodeFromStringOrNull(str: String) =
    decodeFromStringOrNull<T>(serializersModule.serializer(), str)


fun <T> Json.decodeFromStringOrNull(deserializer: DeserializationStrategy<T>, string: String): T? =
    kotlin.runCatching {
        decodeFromString(deserializer, string)
    }.getOrNull()

inline fun <reified T> JsonElement.decode() = icu.dnddl.plugin.genshin.util.Json.decodeFromJsonElement<T>(
    icu.dnddl.plugin.genshin.util.Json.serializersModule.serializer(),
    this
)

inline fun <reified T> String.decode() = icu.dnddl.plugin.genshin.util.Json.decodeFromString<T>(
    icu.dnddl.plugin.genshin.util.Json.serializersModule.serializer(),
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
        (item is Equip && item.star.toInt() == 5).also {
            if (it) sorted.add(item)
        }
    }.filterNot { item ->
        (item is Avatar).also {
            if (it) sorted.add(item)
        }
    }.filterNot { item ->
        (item is Equip && item.star.toInt() == 4).also {
            if (it) sorted.add(item)
        }
    }.filterIsInstance<Equip>().onEach(sorted::add).toList()

    return sorted
}

suspend inline fun <reified T : MiraiUser> T.requireCookie(lazy: () -> Unit = {}): User {
    val userData = getUserData(this.id)
    if (userData.data.cookies.isBlank()) {
        val message = PlainText("请先加好友私聊发送“原神登录”进行登录")
        when (this) {
            is Friend -> sendMessage(message)
            is Member -> group.sendMessage(message)
        }
        lazy()
    }
    return userData
}

fun SignResponse.buildSuccessMessage(uid: Long): String = buildString {
    append("旅行者: $uid")
    if (signInfo.isSign) {
        append("今天已经签过到了哦")
    } else {
        appendLine("签到成功")
        appendLine("今日奖励: ${award.name}x${award.count}")
        append("签到天数: ${signInfo.totalSignDay}")
    }
}

/**
 * 缓存从[url]获取的文件, 并返回缓存, 文件名从[url]自动获取
 */
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

/**
 * 2017-07-01 00:00:00
 */
private const val DYNAMIC_START = 1498838400L

fun dynamictime(id: Long): Long = (id shr 32) + DYNAMIC_START

fun timestamp(sec: Long): OffsetDateTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(sec), ZoneOffset.systemDefault())