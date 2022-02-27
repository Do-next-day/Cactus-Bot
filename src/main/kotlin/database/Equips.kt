package org.laolittle.plugin.genshin.database

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.laolittle.plugin.genshin.util.Json

object Equips : IntIdTable() {
    val name = varchar("Name", 6)
    val star = bool("Star")
    val date = date("Date")
    val description = varchar("Desc", 255)
}

class Equip(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Equip>(Equips)

    var name by Equips.name
    var star by Equips.star
    var date by Equips.date

    @OptIn(ExperimentalSerializationApi::class)
    var description: EquipDescription
    get() = Json.decodeFromString(Equips.description.getValue(this, this::description))
    set(value) = Equips.description.setValue(this, this::description, Json.encodeToString(EquipDescription.serializer(), value))
}

@Serializable
data class EquipDescription(
    val exonym: String,
    val type: EquipType,
)

enum class EquipType {
    /**
     * 弓箭
     */
    Bow,

    /**
     * 法器
     */
    Catalyst,

    /**
     * 大剑
     */
    Claymore,

    /**
     * 长枪
     */
    Pole,

    /**
     * 单手剑
     */
    Sword,
}