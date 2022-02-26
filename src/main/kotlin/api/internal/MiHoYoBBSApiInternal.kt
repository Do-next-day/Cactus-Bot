package org.laolittle.plugin.genshin.api.internal

import com.alibaba.druid.util.Utils
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.util.date.*
import net.mamoe.mirai.utils.error
import org.laolittle.plugin.genshin.GenshinHelper
import kotlin.math.floor
import kotlin.math.round

internal val client = HttpClient(OkHttp)

internal val logger by GenshinHelper::logger

internal var APP_VER = "2.20.1"
    private set

internal const val BBS_URL = "https://bbs.mihoyo.com/"

suspend fun getAppVersion(flush: Boolean = false): String? =
    runCatching {
        val home: String = client.get(BBS_URL)
        val cssPath = BBS_URL + Regex("""<script type="text/javascript" src="(.+?)"></script>""").findAll(home)
            .first { m -> "mainPage.js" in m.value }.groupValues[1]
        Regex("e.version=\"(.+?)\"").find(client.get<String>(cssPath))?.groupValues?.get(1)?.also { ver ->
            if (flush) APP_VER = ver
        }
    }.onFailure { logger.error { "更新版本信息失败" } }.getOrNull()


internal fun getDS(q: String, b: String): String {
    val n = "xV8v4Qu54lUKrEYFZkJhB8cuOh9Asafs"
    val t = round(getTimeMillis() / 1000F)
    val r = floor(Math.random() * 900000 + 100000)
    val ds = Utils.md5("salt=${n}&t=${t}&r=${r}&b=${b}&q=${q}")
    return "${t},${r},${ds}"
}