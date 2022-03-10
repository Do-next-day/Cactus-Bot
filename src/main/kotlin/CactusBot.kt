package icu.dnddl.plugin.genshin

import icu.dnddl.plugin.genshin.api.internal.getAppVersion
import icu.dnddl.plugin.genshin.database.User
import icu.dnddl.plugin.genshin.database.Users
import icu.dnddl.plugin.genshin.database.cactusTransaction
import icu.dnddl.plugin.genshin.mirai.AllMessageListener
import icu.dnddl.plugin.genshin.mirai.FriendMessageListener
import icu.dnddl.plugin.genshin.mirai.GroupMessageListener
import icu.dnddl.plugin.genshin.service.*
import icu.dnddl.plugin.genshin.util.Json
import icu.dnddl.plugin.genshin.util.cacheFolder
import icu.dnddl.plugin.genshin.util.settingFile
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.datetime.atTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.serialization.serializer
import net.mamoe.mirai.console.plugin.description.PluginDependency
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info
import net.mamoe.mirai.utils.warning
import java.time.LocalDate as JLocalDate

object CactusBot : KotlinPlugin(JvmPluginDescription(
    id = "icu.dnddl.plugin.CactusBot",
    name = "Cactus-Bot",
    version = "1.0",
) {
    author("LaoLittle")
    dependsOn(
        PluginDependency("org.laolittle.plugin.SkikoMirai", ">=1.0.2", true)
    )
}) {
    override fun onEnable() {
        kotlin.runCatching { Class.forName("org.laolittle.plugin.SkikoMirai") }
            .onFailure {
                logger.warning {
                    """
                    未找到前置插件: SkikoMirai, 已切换成文字输出
                    如要使用图片输出, 请下载前置插件 https://github.com/LaoLittle/SkikoMirai 并修改插件配置
                """.trimIndent()
                }
                CactusConfig.image = false
                CactusConfig.save()
            }

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

        cactusTransaction { // 为每位用户分发树脂提醒服务
            User.all().forEach {
                GenshinResinPromptingService(it).start()
            }
        }
    }

    override fun onDisable() {
        PluginDispatcher.cancel()
        GenshinGachaCache.cancel()
        if (CactusConfig.autoSign) GenshinSignProver.cancel()

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