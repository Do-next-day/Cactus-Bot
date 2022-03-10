package icu.dnddl.plugin.genshin.database

import icu.dnddl.plugin.genshin.model.GachaImages
import icu.dnddl.plugin.genshin.model.GachaImages.gachaImage
import icu.dnddl.plugin.genshin.util.Json
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.skia.*

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

    override operator fun compareTo(other: GachaItem): Int {
        if (this === other) return 0
        when (other) {
            is Avatar ->
                return if (this.star && other.star) 0
                else if (this.star) 1
                else if (other.star) -1
                else 0

            is Equip ->
                return if (this.star) 1
                else if (other.star.toInt() == 5) -1
                else 1
        }
        return 0
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
    val exonym: String, // 中文名
    val alias: String, // 别名
    val element: AvatarElement, // 元素
) {
    override fun toString(): String = Json.encodeToString(serializer(), this)
}

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