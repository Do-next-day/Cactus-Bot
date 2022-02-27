package org.laolittle.plugin.genshin.model

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.serialization.ExperimentalSerializationApi
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
import org.laolittle.plugin.genshin.model.Tenti.getCard
import org.laolittle.plugin.genshin.util.Json
import org.laolittle.plugin.genshin.util.PluginDispatcher
import org.laolittle.plugin.genshin.util.decodeFromStringOrNull

object GachaSimulator {
    @OptIn(ExperimentalSerializationApi::class)
    fun gachaCharacter(userId: Long, type: Int, times: Int): List<Avatar> {
        val got = mutableListOf<Avatar>()
        var up = 0
        transaction(GenshinHelper.db) {
            val userEntity = User.findById(userId) ?: User.new(userId) {
                card = 1000
                data = UserData().toString()
            }

            val userData: UserData = Json.decodeFromStringOrNull(userEntity.data) ?: UserData()
            if (userEntity.card >= times) {
                val thisGacha = Gacha[type]

                val upAvatar = Avatar[thisGacha.up]
                up = upAvatar.id.value
                val avatars =
                    (Avatar.find { Avatars.star eq false and (Avatars.date lessEq thisGacha.date) } + specialStarAvatars)
                        .toList() + List(5) { upAvatar }

                while (got.size < times) {
                    val per = getProb(userData.gachaTimes)
                    val randomNum = Math.random()
                    val single = avatars.random()
                    if ((randomNum <= per && single.star)) {
                        userData.gachaTimes = 0
                        userData.characterFloor = if (single.id.value != thisGacha.up && !userData.characterFloor) {
                            got.add(single)
                            userData.characters[single] = userData.characters[single] + 1
                            true
                        } else {
                            got.add(upAvatar)
                            userData.characters[upAvatar] = userData.characters[upAvatar] + 1
                            false
                        }
                    } else if ((randomNum > per && !single.star)) {
                        userData.gachaTimes++
                        got.add(single)
                    }
                }

                userEntity.card = userEntity.card - times
                userEntity.data = userData.toString()
            }
        }

        val sorted = mutableListOf<Avatar>()

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

    fun renderGachaImage(avatars: List<Avatar>): Image {
        val w = 1920F
        val h = 1080F
        return Surface.makeRasterN32Premul(w.toInt(), h.toInt()).apply {
            canvas.apply {
                drawImageRect(SETTLEMENT_BACKGROUND, Rect.makeWH(w, h))
                when (avatars.size) {
                    1 -> {
                        TODO()
                    }
                    10 -> {
                        //  val num = it.toIntOrNull() ?: return@startsWith
                        val foo = mutableListOf<Deferred<Image>>()

                        val offset = 1400F
                        val gold = GOLD
                        val purple = PURPLE
                        PluginDispatcher.runBlocking {
                            avatars.forEach {
                                foo.add(async {
                                    it.getCard()
                                })
                            }
                            for (i in avatars.size - 1 downTo 0) {
                                val times = (avatars.size - 1 - i) * 145
                                drawImageRect(
                                    foo[i].await(),
                                    Rect.makeXYWH(offset + 110 - times, 237F, 138F, 606F)
                                )
                                drawImage(if (avatars[i].star) gold else purple, offset - times, 0f)
                            }
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