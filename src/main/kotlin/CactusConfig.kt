package org.laolittle.plugin.genshin

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object CactusConfig : ReadOnlyPluginConfig("GenshinPluginConfig") {
    @ValueDescription("机器人昵称")
    val botName by value("派蒙")

    @ValueDescription("允许用户不登陆进行部分操作")
    val allowAnonymous by value(true)

    @ValueDescription("开启满树脂推送")
    val resinRecoveredPush by value(true)

    @ValueDescription("开启自动签到")
    val autoSign by value(true)
}