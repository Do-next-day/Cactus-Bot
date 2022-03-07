package icu.dnddl.plugin.genshin.database

import icu.dnddl.plugin.genshin.util.Json
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.skia.Image

object Equips : IntIdTable() {
    val name = varchar("Name", 6)
    val star = integer("Star")
    val date = date("Date")
    val description = varchar("Desc", 255)
}

class Equip(id: EntityID<Int>) : IntEntity(id), GachaItem {
    companion object : IntEntityClass<Equip>(Equips)

    var name by Equips.name
    var star by Equips.star
    var date by Equips.date

    var description: EquipDescription
        get() = Json.decodeFromString(EquipDescription.serializer(), Equips.description.getValue(this, ::description))
        set(value) = Equips.description.setValue(
            this, ::description, Json.encodeToString(EquipDescription.serializer(), value)
        )

    override fun getCard(): Image {
        TODO("Not yet implemented")
    }
}

@Serializable
data class EquipDescription(
    val exonym: String,
    val type: EquipType,
) {
    override fun toString(): String = Json.encodeToString(serializer(), this)
}

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