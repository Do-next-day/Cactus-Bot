package org.laolittle.plugin.genshin.database

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.skia.*
import org.laolittle.plugin.genshin.model.GachaImages
import org.laolittle.plugin.genshin.model.GachaImages.gachaImage
import org.laolittle.plugin.genshin.util.Json

object Avatars : IntIdTable() {
    val name = varchar("Name", 6)
    val star = bool("Star")
    val date = date("Date")
    val description = varchar("Desc", 255)
}

class Avatar(id: EntityID<Int>) : IntEntity(id), GachaItem {
    companion object : IntEntityClass<Avatar>(Avatars)

    var name by Avatars.name
    var star by Avatars.star
    var date by Avatars.date

    var description: AvatarDescription
        get() = Json.decodeFromString(AvatarDescription.serializer(), Avatars.description.getValue(this, ::description))
        set(value) = Avatars.description.setValue(
            this, ::description, Json.encodeToString(AvatarDescription.serializer(), value)
        )

    operator fun compareTo(other: Avatar): Int {
        return if (star && other.star) 0
        else if (star) -1
        else if (other.star) 1
        else 0
    }

    override fun getCard(): Image {

        val character = gachaImage
        val sprite = GachaImages.GACHA_ATLAS_SPRITE

        val w = 235
        val h = (w * 4.37).toInt()

        return Surface.makeRasterN32Premul(w, h).apply {
            val dst = Rect.makeWH(w.toFloat(), h.toFloat())
            val paint = Paint().apply {
                blendMode = BlendMode.SRC_ATOP
            }
            canvas.apply {
                drawImageRect(
                    sprite, GachaImages.GACHA_PURE_BG, dst
                )

                drawImageRect(
                    sprite, GachaImages.GACHA_BACKDROP, dst, paint
                )

                drawImage(character, -40f, 17F, paint)

                drawImageRect(sprite, GachaImages.STARRY, dst, paint.apply {
                    colorFilter = ColorFilter.makeMatrix(
                        ColorMatrix(
                            3F, 0F, 0F, 0F, 0F,
                            0F, 3F, 0F, 0F, 0F,
                            0F, 0F, 3F, 0F, 0F,
                            0F, 0F, 0F, 0.15F, 0F,
                        )
                    )
                })
            }
        }.makeImageSnapshot()
    }
}

@Serializable
data class AvatarDescription(
    val exonym: String,
    val element: AvatarElement,
)

@Serializable
@Suppress("unused")
enum class AvatarElement {
    /**
     * 火
     */
    Pyro,

    /**
     * 水
     */
    Hydro,

    /**
     * 风
     */
    Anemo,

    /**
     * 电
     */
    Electro,

    /**
     * 冰
     */
    Cryo,

    /**
     * 岩
     */
    Geo,

    /**
     * 草（？）
     */
}

internal val specialStarAvatars by lazy {
    arrayListOf<Avatar>().apply {
        for (i in 16..20) add(Avatar[i])
    }
}