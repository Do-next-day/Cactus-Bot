package icu.dnddl.plugin.genshin.database

import com.alibaba.druid.pool.DruidDataSource
import icu.dnddl.plugin.genshin.CactusBot
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import javax.sql.DataSource

private val cactusDatabase: Database by lazy {
    val dataSource = DruidDataSource()
    dataSource.url = "jdbc:sqlite:${CactusBot.dataFolder}/genshin.sqlite"
    dataSource.driverClassName = "org.sqlite.JDBC"
    Database.connect(dataSource as DataSource).also {
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        transaction(it) {
            SchemaUtils.create(
                Users,
                Avatars,
                Equips,
                Gachas,
                GachasEquip,
            )
        }
    }
}

fun <T> cactusTransaction(statement: Transaction.() -> T) = transaction(cactusDatabase, statement)

suspend fun <T> cactusSuspendedTransaction(statement: suspend Transaction.() -> T) =
    newSuspendedTransaction(Dispatchers.IO, cactusDatabase, null, statement)