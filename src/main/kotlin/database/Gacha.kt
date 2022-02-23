package org.laolittle.plugin.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object Gachas : IntIdTable() {
    val name = char("Name", 5)
    val up = integer("Up")
    val date = date("Date")
}

class Gacha(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Gacha>(Gachas)

    var name by Gachas.name
    var up by Gachas.up
    var date by Gachas.date
}