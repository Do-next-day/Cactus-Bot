package org.laolittle.plugin.genshin.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object Weapons : IntIdTable() {
    val name = varchar("Name", 6)
    val star = bool("Star")
    val date = date("Date")
    val description = varchar("Desc", 255)
}

class Weapon(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Weapon>(Weapons)

    var name by Weapons.name
    var star by Weapons.star
    var date by Weapons.date
    var description by Weapons.description
}