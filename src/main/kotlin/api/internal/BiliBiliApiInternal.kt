package icu.dnddl.plugin.genshin.api.internal

import icu.dnddl.plugin.genshin.util.Json
import io.ktor.client.request.*
import io.ktor.http.*

internal suspend inline fun biliGet(
    url: String,
    cookie: String? = null,
    block: HttpRequestBuilder.() -> Unit = {}
) = Json.decodeFromString(MiHoYoBBSResponse.serializer(), client.get(url) {
    userAgent("Mozilla/5.0 (Windows NT 6.2; Win64; x64; rv:16.0.1) Gecko/20121011 Firefox/16.0.1")
    accept(ContentType.Application.Json)
    headers {
        cookie?.let { set(HttpHeaders.Cookie, it) }
    }
    block()
})