package org.laolittle.plugin.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.laolittle.plugin.GenshinHelper
import org.laolittle.plugin.database.*
import kotlin.random.Random
import net.mamoe.mirai.contact.User as UserSubject

object GachaSimulator {
    @OptIn(ExperimentalSerializationApi::class)
    fun UserSubject.gachaCharacter(type: Int, times: Int): List<Character> {
        val got = mutableListOf<Character>()
        transaction(GenshinHelper.db) {
            val userEntity = User.findById(this@gachaCharacter.id) ?: User.new(this@gachaCharacter.id) {
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

                val characters =
                    ((Character.find { Characters.star eq false and (Characters.date lessEq thisGacha.date) } + specialCharacters)
                        .toSet().toList() + List(5) { Character[thisGacha.up] })

                while (got.size < times) {
                    val per = getProb(gaTimes)
                    val randomNum = Random.nextDouble(1.0)
                    val single = characters.random()
                    if ((randomNum <= per && single.star)) {
                        gaTimes = 0
                        userData[single] = userData[single] + 1
                        if (single.id.value != thisGacha.up && userData["floor"] == 0) {
                            userData["floor"] = 1
                            got.add(single)
                        } else {
                            userData["floor"] = 0
                            got.add(Character[thisGacha.up])
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

        return got
    }

    fun gachaWeapon(type: Int) {

    }

    private fun getProb(times: Int): Double {
        if (times in 0..70) return 0.006
        val foo = (0.994 / 210) * (times - 70)
        return foo + getProb(times - 1)
    }
}