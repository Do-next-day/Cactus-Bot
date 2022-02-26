package org.laolittle.plugin.genshin.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.skia.Image
import org.jetbrains.skia.Rect
import org.jetbrains.skia.Surface
import org.laolittle.plugin.genshin.GenshinHelper
import org.laolittle.plugin.genshin.database.*
import org.laolittle.plugin.genshin.model.GachaImages.SETTLEMENT_BACKGROUND
import org.laolittle.plugin.genshin.model.Tenti.GOLD
import org.laolittle.plugin.genshin.model.Tenti.PURPLE
import org.laolittle.plugin.genshin.model.Tenti.card

object GachaSimulator {
    @OptIn(ExperimentalSerializationApi::class)
    fun gachaCharacter(userId: Long, type: Int, times: Int): List<Character> {
        val got = mutableListOf<Character>()
        var up = 0
        transaction(GenshinHelper.db) {
            val userEntity = User.findById(userId) ?: User.new(userId) {
                card = 1000
                data = buildJsonObject {
                    put("times", 0)
                    put("floor", 0)
                }.toString()
            }

            val userData: JsonIntMap = Json.decodeFromString(userEntity.data)
            if (userEntity.card >= times) {
                var gaTimes = userData["times"] ?: 1
                val thisGacha = Gacha[type]

                val upCharacter = Character[thisGacha.up]
                up = upCharacter.id.value
                val characters =
                    (Character.find { Characters.star eq false and (Characters.date lessEq thisGacha.date) } + specialStarCharacters)
                        .toList() + List(5) { upCharacter }

                while (got.size < times) {
                    val per = getProb(gaTimes)
                    val randomNum = Math.random()
                    val single = characters.random()
                    if ((randomNum <= per && single.star)) {
                        gaTimes = 0
                        userData[single] = userData[single] + 1
                        userData["floor"] = if (single.id.value != thisGacha.up && userData["floor"] == 0) {
                            got.add(single)
                            1
                        } else {
                            got.add(upCharacter)
                            0
                        }
                    } else if ((randomNum > per && !single.star)) {
                        gaTimes++
                        got.add(single)
                    }
                }

                userEntity.card = userEntity.card - times
                userData["times"] = gaTimes
                userEntity.data = Json.encodeToString(userData)
            }
        }

        val sorted = mutableListOf<Character>()

        got.filter { it.id.value == up }.forEach(sorted::add)
        got.filter { it.star && it.id.value != up }.forEach(sorted::add)
        got.filter { !it.star }.forEach(sorted::add)

        return sorted
    }

    @Suppress("unused", "unused_parameter")
    fun gachaWeapon(userId: Long, type: Int, times: Int) {
        // TODO: 2022/2/24 gachaWeapon
    }

    private fun getProb(times: Int): Double {
        require(times in 0..90) { "Gacha times must be within 0 to 90" }
        if (times in 0..70) return 0.006
        val foo = (0.994 / 210) * (times - 70)
        return foo + getProb(times - 1)
    }

    fun renderGachaImage(characters: List<Character>): Image {
        val w = 1920F
        val h = 1080F
        return Surface.makeRasterN32Premul(w.toInt(), h.toInt()).apply {
            canvas.apply {
                drawImageRect(SETTLEMENT_BACKGROUND, Rect.makeWH(w, h))
                when (characters.size) {
                    1 -> {
                        TODO()
                    }
                    10 -> {
                        //  val num = it.toIntOrNull() ?: return@startsWith
                        val offset = 1400F
                        for (i in characters.size - 1 downTo 0) {
                            val times = (characters.size - 1 - i) * 145
                            drawImageRect(
                                characters[i].card,
                                Rect.makeXYWH(offset + 110 - times, 236.8F, 138F, 606F)
                            )
                            drawImage(if (characters[i].star) GOLD else PURPLE, offset - times, 0f)
                        }
                    }
                }
            }
        }.makeImageSnapshot()
    }
}

/*
private fun <T : Any> T.clone(replaceArgs: Map<KProperty1<T, *>, Any> = emptyMap()): T = javaClass.kotlin.run {
    val consParams = primaryConstructor!!.parameters
    val mutableProperties = memberProperties.filterIsInstance<KMutableProperty1<T, Any?>>()
    val allValues = memberProperties
        .filter { it in mutableProperties || it.name in consParams.map(KParameter::name) }
        .associate { it.name to (replaceArgs[it] ?: it.get(this@clone)) }
    primaryConstructor!!.callBy(consParams.associateWith { allValues[it.name] }).also { newInstance ->
        for (prop in mutableProperties) {
            prop.set(newInstance, allValues[prop.name])
        }
    }
}*/