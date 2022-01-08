package org.laolittle.plugin.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable() {
    val card = integer("Card")
    val data = varchar("Data", 255)
}

class User(id: EntityID<Int>) : IntEntity(id){
    companion object: IntEntityClass<User>(Users)
    var card by Users.card
    var data by Users.data
}