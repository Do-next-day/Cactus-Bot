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

object Avatars : IntIdTable() {
    val name = varchar("Name", 6)
    val star = bool("Star")
    val date = date("Date")
    val description = varchar("Desc", 255)
}

class Avatar(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Avatar>(Avatars)

    var name by Avatars.name
    var star by Avatars.star
    var date by Avatars.date

    @OptIn(ExperimentalSerializationApi::class)
    var description: AvatarDescription
    get() = Json.decodeFromString(Avatars.description.getValue(this, this::description))
    set(value) = Avatars.description.setValue(this, this::description, Json.encodeToString(AvatarDescription.serializer(), value))

    operator fun compareTo(other: Avatar): Int {
        return if (star && other.star) 0
        else if (star) -1
        else if (other.star) 1
        else 0
    }
}

@Serializable
@Suppress("unused")
enum class AvatarElement {
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
data class AvatarDescription(
    val exonym: String,
    val element: AvatarElement,
    val skills: String,
    val from: String,
)

internal val specialStarAvatars by lazy {
    arrayListOf<Avatar>().apply {
        for (i in 16..20) add(Avatar[i])
    }
}