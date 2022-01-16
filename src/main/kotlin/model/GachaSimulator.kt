@file: Suppress("unused")

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

            val userData: MutableMap<String, Int> = Json.decodeFromString(userEntity.data)
            if (userEntity.card >= times) {
                var gaTimes = userData["times"]?.plus(times) ?: times
                val itGacha = Gacha[type]
                val characters =
                (Character.find { (Characters.id eq itGacha.up) or (Characters.star eq false and (Characters.date lessEq itGacha.date)) } + Character[16] + Character[17] + Character[18] + Character[19] + Character[20] + Character[21])
                    .toSet()
                while (gets.size < times) {
                    gaTimes++
                    val per = if (gaTimes < 50) 6 else if (gaTimes < 70) 20 else if (gaTimes < 89) 300 else 1000
                    val randomNum = (1..1000).random()
                    val single = Character[characters.random().id.value]
                    if ((randomNum <= per && single.star)) {
                        gaTimes = 0
                        userData[single.id.value.toString()] = userData[single.id.value.toString()]?.plus(1) ?: 1
                        gets.add(single.id)
                    }else if ((randomNum > per && !single.star)) gets.add(single.id)
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
}