package org.laolittle.plugin.genshin

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
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Database.Companion.connectPool
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.laolittle.plugin.genshin.command.GenshinAdd
import org.laolittle.plugin.genshin.database.*
import org.laolittle.plugin.genshin.model.GachaSimulator.gachaCharacter
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
                    "十连", "单抽" -> {
                        val times = if (result == "十连") 10 else 1

                        val entities = newSuspendedTransaction(Dispatchers.IO, db) {
                            sender.gachaCharacter(1, times)
                        }

                        buildForwardMessage {
                            entities.forEach { c ->
                                add(bot, PlainText(c.name))
                            }
                        }.also { subject.sendMessage(it) }
                    }
                }
            }

            "人物列表" {
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

    private fun init(){
        Config.reload()
        GenshinAdd.register()
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