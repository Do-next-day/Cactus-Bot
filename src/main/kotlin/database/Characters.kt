package org.laolittle.plugin.genshin.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

object Characters : IntIdTable() {
    val name = varchar("Name", 6)
    val star = bool("Star")
    val date = date("Date")
    val description = varchar("Desc", 255)
}

class Character(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Character>(Characters)

    var name by Characters.name
    var star by Characters.star
    var date by Characters.date
    var description by Characters.description
}

internal val specialStarCharacters by lazy {
    arrayListOf<Character>().apply {
        for (i in 16..20) add(Character[i])
    }
}