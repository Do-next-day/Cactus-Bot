package icu.dnddl.plugin.genshin.util

import icu.dnddl.plugin.genshin.api.genshin.GenshinBBSApi
import icu.dnddl.plugin.genshin.database.User

suspend fun User.signGenshin() =
    GenshinBBSApi.signGenshinWithAward(genshinUID, GenshinBBSApi.getServerFromUID(genshinUID), data.cookies, data.uuid)

suspend fun User.getDailyNote() = GenshinBBSApi.getDailyNote(genshinUID, data.cookies, data.uuid)