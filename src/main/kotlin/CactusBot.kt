package org.laolittle.plugin.genshin

import io.ktor.client.request.*
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.buildForwardMessage
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import net.mamoe.mirai.utils.info
import org.jetbrains.skia.EncodedImageFormat
import org.laolittle.plugin.genshin.api.GenshinBBSApi
import org.laolittle.plugin.genshin.api.internal.client
import org.laolittle.plugin.genshin.api.internal.getAppVersion
import org.laolittle.plugin.genshin.model.GachaSimulator.gachaCharacter
import org.laolittle.plugin.genshin.model.GachaSimulator.renderGachaImage
import org.laolittle.plugin.genshin.util.characterDataFolder
import org.laolittle.plugin.genshin.util.gachaDataFolder
import org.laolittle.plugin.toExternalResource

object CactusBot : KotlinPlugin(
    JvmPluginDescription(
        id = "org.laolittle.plugin.GenshinHelper",
        name = "Genshin-Helper",
        version = "1.0",
    ) {
        author("LaoLittle")
    }
) {
    private val users = mutableSetOf<Long>()
    override fun onEnable() {
        init()
        logger.info { "Plugin loaded" }


        globalEventChannel().subscribeGroupMessages {
            (startsWith("原神") or startsWith("派蒙")) Listener@{ foo ->
                val result = Regex("""(人物|十连|单抽|查询)(.*)""").find(foo)?.groupValues
                when (result?.get(1)) {
                    "十连" -> {
                        if (!users.add(sender.id)) return@Listener
                        val entities = gachaCharacter(sender.id, 1, 10)
                        renderGachaImage(entities)
                            .toExternalResource(EncodedImageFormat.JPEG).use { ex ->
                                subject.sendMessage(ex.uploadAsImage(subject) + message.quote())
                            }
                        users.remove(sender.id)
                    }
                    "查询" -> {
                        val uid = result[2].replace(Regex("""[\s]+"""), "").toLongOrNull()
                        if (uid == null || uid < 100000100 || uid > 700000000) {
                            subject.sendMessage("请输入正确的uid")
                            return@Listener
                        }
                        val query = kotlin.runCatching {
                            GenshinBBSApi.getPlayerInfo(uid)
                        }.getOrElse {
                            when (it) {
                                is SerializationException -> subject.sendMessage("请求失败！请检查uid是否正确")
                                is IllegalAccessException -> subject.sendMessage("获取失败: ${it.message}")
                                else -> logger.info(it)
                            }
                            return@Listener
                        }

                        subject.sendMessage(buildForwardMessage {
                            query.avatars.forEach { cInfo ->
                                add(bot, buildMessageChain {
                                    add(
                                        client.get<ByteArray>(cInfo.imageUrl).toExternalResource()
                                            .use { subject.uploadImage(it) }
                                    )
                                    add(
                                        """
                                        名称: ${cInfo.name}
                                        命座: ${cInfo.constellation}
                                    """.trimIndent()
                                    )
                                })
                            }
                        })
                    }
                }
            }
        }
    }

    private fun init() {
        launch { getAppVersion(true) }
        CactusConfig.reload()
        CactusData.reload()
        dataFolder.mkdirs()
        gachaDataFolder.mkdir()
        characterDataFolder.mkdir()
    }

}