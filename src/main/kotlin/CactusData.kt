package icu.dnddl.plugin.genshin

import icu.dnddl.plugin.genshin.api.genshin.GenshinBBSApi
import icu.dnddl.plugin.genshin.api.genshin.data.Award
import icu.dnddl.plugin.genshin.database.UserSetting
import icu.dnddl.plugin.genshin.service.PluginDispatcher
import icu.dnddl.plugin.genshin.util.Json
import icu.dnddl.plugin.genshin.util.cacheFolder
import icu.dnddl.plugin.genshin.util.decodeFromStringOrNull
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.serializer
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object CactusData : AutoSavePluginData("GenshinPluginData") {
    @ValueDescription("全局Cookie, 支持多个")
    val cookies by value(mutableSetOf("Cookie1", "Cookie2"))

    val cookie get() = cookies.random()

    val userSetting: MutableMap<Long, UserSetting>

    var awardMonth: Int by value(0)

    var awards: List<Award.AwardItem>

    init {
        val settingFile = CactusBot.dataFolder.resolve("userSettings.json").also { it.createNewFile() }
        userSetting =
            Json.decodeFromStringOrNull(settingFile.readText()) ?: mutableMapOf()
        val saveJson = fun() {
            settingFile.writeText(
                Json.encodeToString(
                    Json.serializersModule.serializer(),
                    userSetting
                )
            )
        }

        awards = Json.decodeFromStringOrNull(
            Json.serializersModule.serializer(),
            cacheFolder.resolve("awards.json").also { if (!it.isFile) it.createNewFile() }.readText()
        ) ?: PluginDispatcher.runBlocking {
            GenshinBBSApi.getAwards().also {
                awardMonth = it.month
            }.awards
        }

        if (!settingFile.isFile) saveJson()

        PluginDispatcher.launch {
            while (isActive) {
                delay(5 * 60 * 1000)
                saveJson()
            }
        }
    }
}