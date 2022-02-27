package org.laolittle.plugin.genshin

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

object Data : AutoSavePluginData("GenshinPluginData") {
    var cookies by value("")
}