package icu.dnddl.plugin.genshin

import icu.dnddl.plugin.genshin.api.internal.getAppVersion
import icu.dnddl.plugin.genshin.mirai.AllMessageListener
import icu.dnddl.plugin.genshin.mirai.FriendMessageListener
import icu.dnddl.plugin.genshin.mirai.GroupMessageListener
import icu.dnddl.plugin.genshin.service.GenshinGachaCache
import icu.dnddl.plugin.genshin.service.GenshinSignProver
import icu.dnddl.plugin.genshin.service.PluginDispatcher
import icu.dnddl.plugin.genshin.service.aDay
import icu.dnddl.plugin.genshin.util.Json
import icu.dnddl.plugin.genshin.util.cacheFolder
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.datetime.atTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.serializer
import net.mamoe.mirai.console.plugin.description.PluginDependency
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info
import java.time.LocalDate as JLocalDate

object CactusBot : KotlinPlugin(JvmPluginDescription(
    id = "icu.dnddl.plugin.CactusBot",
    name = "Cactus-Bot",
    version = "1.0",
) {
    author("LaoLittle")
    dependsOn(
        PluginDependency("icu.dnddl.plugin.SkikoMirai", ">=1.0.2", true)
    )
}) {
    override fun onEnable() {
        launch { getAppVersion(true) }
        CactusConfig.reload()
        CactusData.reload()
        dataFolder.mkdirs()

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
        PluginDispatcher.cancel()
        GenshinGachaCache.cancel()
        if (CactusConfig.autoSign) GenshinSignProver.cancel()

        val settingFile = CactusBot.configFolder.resolve("userSettings.json")
        settingFile.writeText(
            Json.encodeToString(
                Json.serializersModule.serializer(),
                CactusData.userSetting
            )
        )

        Json.encodeToString(
            Json.serializersModule.serializer(),
            CactusData.awards
        ).also {
            cacheFolder.resolve("awards.json").writeText(it)
        }

        logger.info { "All services closed successfully" }
    }
}