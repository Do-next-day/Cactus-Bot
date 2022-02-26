package org.laolittle.plugin.genshin

import com.alibaba.druid.pool.DruidDataSource
import kotlinx.coroutines.launch
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.utils.ExternalResource.Companion.sendAsImageTo
import net.mamoe.mirai.utils.info
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Database.Companion.connectPool
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.skia.EncodedImageFormat
import org.laolittle.plugin.genshin.api.internal.getAppVersion
import org.laolittle.plugin.genshin.database.*
import org.laolittle.plugin.genshin.model.GachaSimulator.gachaCharacter
import org.laolittle.plugin.genshin.model.GachaSimulator.renderGachaImage
import org.laolittle.plugin.genshin.util.characterDataFolder
import org.laolittle.plugin.genshin.util.gachaDataFolder
import org.laolittle.plugin.toExternalResource
import java.sql.Connection
import javax.sql.ConnectionPoolDataSource

object GenshinHelper : KotlinPlugin(
    JvmPluginDescription(
        id = "org.laolittle.plugin.GenshinHelper",
        name = "Genshin-Helper",
        version = "1.0",
    ) {
        author("LaoLittle")
    }
) {
    private val dataSource = DruidDataSource()
    val db: Database
    override fun onEnable() {
        init()
        logger.info { "Plugin loaded" }
        globalEventChannel().subscribeGroupMessages {
            startsWith("原神") { foo ->
                when (val result = Regex("""(人物|十连|单抽)""").find(foo)?.groupValues?.get(1)) {
                    "十连" -> {
                        val entities = gachaCharacter(sender.id, 1, 10)
                        renderGachaImage(entities)
                            .toExternalResource(EncodedImageFormat.JPEG)
                            .sendAsImageTo(subject)
                    }
                }
            }

        }
    }

    private fun init() {
        launch { getAppVersion(true) }
        Config.reload()
        dataFolder.mkdirs()
        gachaDataFolder.mkdir()
        characterDataFolder.mkdir()
    }

    init {
        dataSource.url = "jdbc:sqlite:$dataFolder/genshin.sqlite"
        dataSource.driverClassName = "org.sqlite.JDBC"
        db = connectPool(dataSource as ConnectionPoolDataSource)
        TransactionManager.manager.defaultIsolationLevel =
            Connection.TRANSACTION_SERIALIZABLE
        transaction(db) {
            SchemaUtils.create(
                Users,
                Characters,
                Weapons,
                Gachas,
                GachasWeapon,
            )
        }
    }
}