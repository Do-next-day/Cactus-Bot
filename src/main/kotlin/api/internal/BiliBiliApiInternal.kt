package org.laolittle.plugin.genshin.api.internal

import io.ktor.client.request.*
import io.ktor.http.*
import org.laolittle.plugin.genshin.util.Json

internal suspend inline fun biliGet(
    url: String,
    cookie: String? = null,
    block: HttpRequestBuilder.() -> Unit = {}
) = Json.decodeFromString(Response.serializer(), client.get(url) {
    headers.apply {
        userAgent("Mozilla/5.0 (Windows NT 6.2; Win64; x64; rv:16.0.1) Gecko/20121011 Firefox/16.0.1")
        accept(ContentType.Application.Json)
        cookie?.let {set(HttpHeaders.Cookie, it)  }
    }
    block()
})