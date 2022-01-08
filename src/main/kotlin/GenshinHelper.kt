package org.laolittle.plugin

import com.alibaba.druid.pool.DruidDataSource
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.info
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.laolittle.plugin.command.GenshinAdd
import org.laolittle.plugin.database.Character
import org.laolittle.plugin.database.Characters
import java.sql.Connection
import javax.sql.DataSource

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
    val db: org.jetbrains.exposed.sql.Database
    override fun onEnable() {
        GenshinAdd.register()
        logger.info { "Plugin loaded" }
        globalEventChannel().subscribeGroupMessages {
            "人物列表"{
                subject.sendMessage(buildMessageChain {
                    transaction {
                        Character.all().forEach {
                            add(it.name)
                        }
                    }
                })
            }
        }
    }

    init {
        dataSource.url = "jdbc:sqlite:$dataFolder/genshin.sqlite"
        dataSource.driverClassName = "org.sqlite.JDBC"
        db = org.jetbrains.exposed.sql.Database.connect(dataSource as DataSource)
        TransactionManager.manager.defaultIsolationLevel =
            Connection.TRANSACTION_SERIALIZABLE
        transaction(db) {
            SchemaUtils.create(Characters)
        }
    }
}