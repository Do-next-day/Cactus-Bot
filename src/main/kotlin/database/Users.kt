package org.laolittle.plugin.genshin.database

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.laolittle.plugin.genshin.util.Json

object Users : LongIdTable() {
    val card = integer("Card")
    val data = varchar("Data", 255)
}

class User(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<User>(Users)

    var card by Users.card
    var data by Users.data
}

@Serializable
data class UserData(
    var gachaTimes: Int = 0,
    var characterFloor: Boolean = false,
    var weaponFloor: Boolean = false,
    val characters: MutableIntMap = mutableMapOf(),
) {
    var miHoYoCookies = ""
        private set

    fun setCookies(cookies: String) {
        miHoYoCookies = cookies
    }

    override fun toString() = Json.encodeToString(serializer(), this)
}

internal typealias MutableIntMap = MutableMap<Int, Int>

operator fun MutableIntMap.get(c: Avatar) = this[c.id.value] ?: 0

operator fun MutableIntMap.set(c: Avatar, r: Int) {
    this[c.id.value] = r
}