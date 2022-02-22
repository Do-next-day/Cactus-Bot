package org.laolittle.plugin.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction
import org.laolittle.plugin.GenshinHelper
import org.laolittle.plugin.database.*
import kotlin.random.Random
import net.mamoe.mirai.contact.User as UserSubject

object GachaSimulator {
    @OptIn(ExperimentalSerializationApi::class)
    fun UserSubject.gachaCharacter(type: Int, times: Int = 1): MutableList<EntityID<Int>> {
        val gets = mutableListOf<EntityID<Int>>()
        transaction(GenshinHelper.db) {
            val userEntity = User.findById(this@gachaCharacter.id) ?: User.new(this@gachaCharacter.id) {
                card = 1000
                data = buildJsonObject { put("times", 0) }.toString()
            }

            val userData: JsonIntMap = Json.decodeFromString(userEntity.data)
            if (userEntity.card >= times) {
                var gaTimes = userData["times"]?.plus(times) ?: times
                val thisGacha = Gacha[type]

                val characters =
                    (Character.find { (Characters.id eq thisGacha.up) or (Characters.star eq false and (Characters.date lessEq thisGacha.date)) } + specialCharacters)
                        .toSet()
                while (gets.size < times) {
                    gaTimes++
                    val per = getProb(gaTimes)
                    val randomNum = Random.nextDouble(1.0)
                    val single = Character[characters.random().id.value]
                    if ((randomNum <= per && single.star)) {
                        gaTimes = 0
                        userData[single] = userData[single] + 1
                        gets.add(single.id)
                    } else if ((randomNum > per && !single.star)) gets.add(single.id)
                }
                userEntity.card = userEntity.card - times
                userData["times"] = gaTimes
                userEntity.data = Json.encodeToString(userData)
            }
        }

        return gets
    }

    fun gachaWeapon(type: Int) {

    }

    private fun getProb(times: Int): Double {
        if (times in 1..70) return 0.006
        val foo = (0.994 / 210) * (times - 70)
        return foo + getProb(times - 1)
    }
}