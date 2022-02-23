package org.laolittle.plugin

import com.alibaba.druid.pool.DruidDataSource
import kotlinx.coroutines.Dispatchers
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.buildForwardMessage
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.info
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.laolittle.plugin.command.GenshinAdd
import org.laolittle.plugin.database.Character
import org.laolittle.plugin.database.Characters
import org.laolittle.plugin.database.Gachas
import org.laolittle.plugin.database.Users
import org.laolittle.plugin.model.GachaSimulator.gachaCharacter
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
                    transaction(db) {
                        Character.all().forEach {
                            add("${it.name}(${it.date})[${if (it.star) "五星" else "四星"}]")
                            add("\n")
                        }
                    }
                })
            }

            "模拟抽卡" Here@{
                newSuspendedTransaction(Dispatchers.IO, db) {
                    val entityIDS = sender.gachaCharacter(1, 10)
                    val characters = mutableListOf<String>()
                    entityIDS.forEach { character ->
                        characters.add(character.name)
                    }
                    /*GachaRenderer.renderGachaResult(characters).toExternalResource().use {
                        subject.sendMessage("抽卡结果")
                        subject.sendImage(it)
                    }*/
                    buildForwardMessage {
                        characters.forEach {
                            add(bot, PlainText(it))
                        }
                    }.also { subject.sendMessage(it) }
                }
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
            SchemaUtils.create(Gachas)
            SchemaUtils.create(Users)
        }
    }
}