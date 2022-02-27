package org.laolittle.plugin.genshin.database

import kotlinx.serialization.Serializable
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

    operator fun compareTo(other: Character): Int {
        return if (star && other.star) 0
        else if (star) -1
        else if (other.star) 1
        else 0
    }
}

@Serializable
@Suppress("unused")
enum class CharacterElement {
    // 火
    Pyro,

    // 水
    Hydro,

    // 风
    Anemo,

    // 电
    Electro,

    // 冰
    Cryo,

    // 岩
    Geo,
    // 草（？）
}

@Serializable
data class CharacterDescription(
    val alias: String,
    val skills: String,
    val from: String,
)

internal val specialStarCharacters by lazy {
    arrayListOf<Character>().apply {
        for (i in 16..20) add(Character[i])
    }
}