package org.laolittle.plugin.database

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Users : LongIdTable() {
    val card = integer("Card")
    val data = varchar("Data", 255)
}

class User(id: EntityID<Long>) : LongEntity(id){
    companion object: LongEntityClass<User>(Users)
    var card by Users.card
    var data by Users.data
}

internal val Json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    allowStructuredMapKeys = true
}

internal typealias JsonIntMap = MutableMap<String, Int>

operator fun JsonIntMap.get(c: Character) = this[c.id.value.toString()] ?: 0

operator fun JsonIntMap.set(c: Character, r: Int) {
    this[c.id.value.toString()] = r
}