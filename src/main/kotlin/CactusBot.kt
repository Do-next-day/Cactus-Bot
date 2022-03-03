package org.laolittle.plugin.genshin

import kotlinx.coroutines.launch
import kotlinx.datetime.atTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.serializer
import net.mamoe.mirai.console.plugin.description.PluginDependency
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info
import org.laolittle.plugin.genshin.api.internal.getAppVersion
import org.laolittle.plugin.genshin.mirai.AllMessageListener
import org.laolittle.plugin.genshin.mirai.FriendMessageListener
import org.laolittle.plugin.genshin.mirai.GroupMessageListener
import org.laolittle.plugin.genshin.service.GenshinGachaCache
import org.laolittle.plugin.genshin.service.GenshinSignProver
import org.laolittle.plugin.genshin.service.aDay
import org.laolittle.plugin.genshin.util.Json
import org.laolittle.plugin.genshin.util.avatarDataFolder
import org.laolittle.plugin.genshin.util.cacheFolder
import org.laolittle.plugin.genshin.util.gachaDataFolder
import java.time.LocalDate as JLocalDate

object CactusBot : KotlinPlugin(JvmPluginDescription(
    id = "org.laolittle.plugin.CactusBot",
    name = "Cactus-Bot",
    version = "1.0",
) {
    author("LaoLittle")
    dependsOn(
        PluginDependency("org.laolittle.plugin.SkikoMirai", ">=1.0.2", true)
    )
}) {
    override fun onEnable() {
        launch { getAppVersion(true) }
        CactusConfig.reload()
        CactusData.reload()
        dataFolder.mkdirs()
        gachaDataFolder.mkdir()
        avatarDataFolder.mkdir()
        cacheFolder.mkdir()

        // Listener
        AllMessageListener.start()
        FriendMessageListener.start()
        GroupMessageListener.start()

        // Services
        val nowDay = JLocalDate.now()
        val dateTime = JLocalDate.ofYearDay(nowDay.year, nowDay.dayOfYear + 1).atStartOfDay().toKotlinLocalDateTime()
        GenshinGachaCache.startAt(dateTime.date.atTime(4, 15), aDay)
        if (CactusConfig.autoSign) GenshinSignProver.startAt(dateTime, aDay)
        logger.info { "Cactus-Bot loaded" }

    }

    override fun onDisable() {
        GenshinGachaCache.cancel()
        if (CactusConfig.autoSign) GenshinSignProver.cancel()

        val settingFile = CactusBot.configFolder.resolve("userSettings.json")
        settingFile.writeText(
            Json.encodeToString(
                Json.serializersModule.serializer(),
                CactusData.userSetting
            )
        )
    }
}