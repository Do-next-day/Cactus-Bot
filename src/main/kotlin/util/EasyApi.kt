package org.laolittle.plugin.genshin.util

import org.laolittle.plugin.genshin.api.genshin.GenshinBBSApi
import org.laolittle.plugin.genshin.database.User

suspend fun User.signGenshin() =
    GenshinBBSApi.signGenshinWithAward(genshinUID, GenshinBBSApi.getServerFromUID(genshinUID), data.cookies, data.uuid)

suspend fun User.getDailyNote() = GenshinBBSApi.getDailyNote(genshinUID, data.cookies, data.uuid)