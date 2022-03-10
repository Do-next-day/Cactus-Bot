package icu.dnddl.plugin.genshin

import icu.dnddl.plugin.genshin.api.bilibili.data.BiliSubscribeData
import icu.dnddl.plugin.genshin.api.genshin.GenshinBBSApi
import icu.dnddl.plugin.genshin.api.genshin.data.Award
import icu.dnddl.plugin.genshin.database.UserSetting
import icu.dnddl.plugin.genshin.service.PluginDispatcher
import icu.dnddl.plugin.genshin.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.serializer
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object CactusData : AutoSavePluginData("GenshinPluginData") {
    @ValueDescription("全局Cookie, 支持多个")
    val cookies by value(mutableSetOf("Cookie1"))

    val cookie get() = cookies.random()

    val userSetting: MutableMap<Long, UserSetting> =
        Json.decodeFromStringOrNull(settingFile.readText()) ?: mutableMapOf()

    var awardMonth: Int by value(0)

    var awards: List<Award.AwardItem>

    val biliSubscribes: MutableMap<Long, BiliSubscribeData> =
        Json.decodeFromStringOrNull(biliSubscribesFile.readText()) ?: mutableMapOf()

    init {
        val saveJson = fun() {
            settingFile.writeText(Json.encodeToString(Json.serializersModule.serializer(), userSetting))
            biliSubscribesFile.writeText(Json.encodeToString(Json.serializersModule.serializer(), biliSubscribes))
        }

        awards = Json.decodeFromStringOrNull(
            Json.serializersModule.serializer(),
            awardsFile.readText()
        ) ?: PluginDispatcher.runBlocking {
            GenshinBBSApi.getAwards().also {
                awardMonth = it.month
            }.awards
        }

        if (!settingFile.isFile || !biliSubscribesFile.isFile) saveJson()

        PluginDispatcher.launch {
            while (isActive) {
                delay(5 * 60 * 1000)
                saveJson()
            }
        }
    }
}