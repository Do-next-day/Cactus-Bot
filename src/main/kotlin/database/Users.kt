package icu.dnddl.plugin.genshin.database

import icu.dnddl.plugin.genshin.util.Json
import icu.dnddl.plugin.genshin.util.decodeFromStringOrNull
import icu.dnddl.plugin.genshin.util.randomUUID
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Users : LongIdTable() {
    val card = integer("Card")
    val genshinUID = long("Uid")
    val data = varchar("Data", 500)
}

class User(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<User>(Users)

    var card by Users.card
    var genshinUID by Users.genshinUID
    var data: UserData
        get() = Json.decodeFromStringOrNull(UserData.serializer(), Users.data.getValue(this, ::data)) ?: UserData()
        set(value) = Users.data.setValue(this, ::data, Json.encodeToString(UserData.serializer(), value))
}

@Serializable
data class UserData(
    var gachaTimes: Int = 0,
    var characterFloor: Boolean = false,
    var weaponFloor: Boolean = false,
    val characters: MutableIntMap = mutableMapOf(),
    var cookies: String = "",
    val uuid: String = randomUUID
) {
    override fun toString() = Json.encodeToString(serializer(), this)
}

@Serializable
data class UserSetting(
    var push: Boolean = true,
    var autoSign: Boolean = true,
    var resinRemind: Long = 30 * 60,
    var pushSubject: Long,
) {
    override fun toString(): String = Json.encodeToString(serializer(), this)
}

suspend fun getUserData(id: Long) = cactusSuspendedTransaction {
    User.findById(id) ?: User.new(id) {
        card = 100
        genshinUID = 0
        data = UserData()
    }
}

internal typealias MutableIntMap = MutableMap<Int, Int>

operator fun MutableIntMap.get(c: Avatar) = this[c.id.value] ?: 0

operator fun MutableIntMap.set(c: Avatar, r: Int) {
    this[c.id.value] = r
}