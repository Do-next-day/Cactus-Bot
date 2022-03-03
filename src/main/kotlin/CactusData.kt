package org.laolittle.plugin.genshin

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object CactusData : AutoSavePluginData("GenshinPluginData") {
    @ValueDescription("全局Cookie, 支持多个")
    val cookies by value(mutableSetOf("Cookie1", "Cookie2"))
    val autoSign by value(mutableSetOf<Long>())

    val cookie get() = cookies.random()
}