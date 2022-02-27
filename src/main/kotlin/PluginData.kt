package org.laolittle.plugin.genshin

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

object PluginData : AutoSavePluginData("GenshinPluginData") {
    var cookies by value("")
}