package org.laolittle.plugin.genshin

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

object CactusData : AutoSavePluginData("GenshinPluginData") {
    var cookies by value("")
    val autoSign by value(mutableSetOf<Long>())
}