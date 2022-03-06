package org.laolittle.plugin.genshin

import net.mamoe.mirai.console.data.ReadOnlyPluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.laolittle.plugin.genshin.service.PluginDispatcher

object CactusConfig : ReadOnlyPluginConfig("GenshinPluginConfig") {
    @ValueDescription("机器人昵称")
    val botName by value("派蒙")

    @ValueDescription("允许用户不登陆进行部分操作")
    val allowAnonymous by value(true)

    @ValueDescription("开启满树脂推送")
    val resinRecoveredPush by value(true)

    @ValueDescription("开启自动签到")
    val autoSign by value(true)

    private val guideResourceFolder = CactusBot.configFolder.resolve("GuideRes").also { it.mkdir() }
    val Contact.guideMessage: MessageChain
        get() {
            var foo = guideResourceFolder
                .resolve("guide.txt")
                .also { if (!it.isFile) it.writeText("请百度如何获取Cookie") }
                .readText()

            val r = Regex("%p(.+?)%")
            val result = r.findAll(foo)
            result.forEach {
                val image = PluginDispatcher.runBlocking {
                    guideResourceFolder.resolve(it.groupValues[1]).uploadAsImage(this@guideMessage)
                }

                foo = foo.replace("%p${it.groupValues[1]}%", image.imageId)
            }
            return foo.deserializeMiraiCode(this)
        }
}