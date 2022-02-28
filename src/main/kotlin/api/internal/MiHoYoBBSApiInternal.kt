package org.laolittle.plugin.genshin.api.internal

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.date.*
import net.mamoe.mirai.utils.error
import org.laolittle.plugin.genshin.CactusBot
import org.laolittle.plugin.genshin.CactusData
import org.laolittle.plugin.genshin.api.ACT_ID_GENSHIN
import org.laolittle.plugin.genshin.util.Json
import org.laolittle.plugin.genshin.util.randomUUID
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import kotlin.random.Random.Default.nextInt

internal val client = HttpClient(OkHttp) {
    engine {
        config {
            readTimeout(20, TimeUnit.SECONDS)
        }
    }
}

internal val logger by CactusBot::logger

internal var LAB_APP_VER = "2.20.1"
    private set

internal const val BBS_URL = "https://bbs.mihoyo.com/"

suspend fun getAppVersion(flush: Boolean = false): String? = runCatching {
    val home: String = client.get(BBS_URL)
    val cssPath = BBS_URL + Regex("""<script type="text/javascript" src="(.+?)"></script>""").findAll(home)
        .first { m -> "mainPage.js" in m.value }.groupValues[1]
    Regex("e.version=\"(.+?)\"").find(client.get<String>(cssPath))?.let { r ->
        r.groupValues[1].also { ver ->
            if (flush) LAB_APP_VER = ver
        }
    }
}.onFailure { logger.error { "更新米游社App版本信息失败!" } }.getOrNull()


private const val API_SALT = "xV8v4Qu54lUKrEYFZkJhB8cuOh9Asafs"
internal fun getDS(url: String, body: String): String {
    val time = getTimeMillis() / 1000
    val random = nextInt(100000, 200000)
    val urlParts = url.split("?")
    val query = if (urlParts.size == 2) urlParts[1] else ""
    val check = md5("salt=${API_SALT}&t=${time}&r=${random}&b=${body}&q=${query}")
    return "${time},${random},${check}"
}

internal suspend inline fun getBBS(
    url: String,
    cookies: String = CactusData.cookies,
    uuid: String = randomUUID,
    block: HttpRequestBuilder.() -> Unit = {}
) = Json.decodeFromString(Response.serializer(), client.get(url) {
    block()
    setHeaders(url, this.body.toString(), cookies, uuid)
})

internal suspend inline fun postBBS(
    url: String,
    cookies: String = CactusData.cookies,
    uuid: String = randomUUID,
    block: HttpRequestBuilder.() -> Unit = {}
) = Json.decodeFromString(Response.serializer(), client.post(url) {
    block()
    setHeaders(url, this.body.toString(), cookies, uuid)
    headers["Content-Type"] = "application/json;charset=UTF-8"
})

internal fun HttpRequestBuilder.setHeaders(
    url: String, body: String = "", cookies: String = CactusData.cookies, uuid: String = randomUUID
) {
    headers.apply {
        userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) miHoYoBBS/$LAB_APP_VER")
        append("X-Requested-With", "com.mihoyo.hyperion")
        append("x-rpc-device_id", uuid)
        append("x-rpc-client_type", "5")
        append("x-rpc-app_version", LAB_APP_VER)
        append("DS", getDS(url, body))
        set(
            HttpHeaders.Referrer,
            "https://webstatic.mihoyo.com/bbs/event/signin-ys/index.html?bbs_auth_required=true&act_id=$ACT_ID_GENSHIN&utm_source=bbs&utm_medium=mys&utm_campaign=icon"
        )
        accept(ContentType.Application.Json)
        // set(HttpHeaders.AcceptEncoding, "gzip, deflate")
        set(HttpHeaders.Cookie, cookies)
    }
}

private fun md5(content: String): String {
    val hash = MessageDigest.getInstance("MD5").digest(content.toByteArray())
    val hex = StringBuilder(hash.size * 2)
    for (byte in hash) {
        val foo = Integer.toHexString(byte.toInt())
        val str = if (byte > 0x10) foo
        else "0$foo"
        hex.append(str.substring(str.length - 2))
    }
    return hex.toString()
}