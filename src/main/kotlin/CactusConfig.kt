package org.laolittle.plugin.genshin

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object CactusConfig : ReadOnlyPluginConfig("GenshinPluginConfig") {
    @ValueDescription("允许用户不登陆进行操作")
    val allowAnonymous by value(true)
}