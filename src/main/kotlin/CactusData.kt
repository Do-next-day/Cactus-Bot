package org.laolittle.plugin.genshin

import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.serializer
import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value
import org.laolittle.plugin.genshin.database.UserSetting
import org.laolittle.plugin.genshin.service.PluginDispatcher
import org.laolittle.plugin.genshin.util.Json
import org.laolittle.plugin.genshin.util.decodeFromStringOrNull

object CactusData : AutoSavePluginData("GenshinPluginData") {
    @ValueDescription("全局Cookie, 支持多个")
    val cookies by value(mutableSetOf("Cookie1", "Cookie2"))

    val cookie get() = cookies.random()

    val userSetting: MutableMap<Long, UserSetting>

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

        if (!settingFile.isFile) saveJson()

        PluginDispatcher.launch {
            while (isActive) {
                delay(5 * 60 * 1000)
                saveJson()
            }
        }
    }
}