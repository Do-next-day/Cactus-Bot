package org.laolittle.plugin.genshin.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.skia.Image

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

object GachasWeapon : IntIdTable() {
    val name = char("Name", 5)
    val up = integer("Up")
    val date = date("Date")
}

class GachaWeapon(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<GachaWeapon>(GachasWeapon)

    var name by GachasWeapon.name
    var up by GachasWeapon.up
    var date by GachasWeapon.date
}

interface GachaItem {
    fun getCard(): Image
}