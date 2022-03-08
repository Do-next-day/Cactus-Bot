package icu.dnddl.plugin.genshin.mirai

import icu.dnddl.plugin.genshin.CactusBot
import icu.dnddl.plugin.genshin.CactusConfig
import icu.dnddl.plugin.genshin.CactusData
import icu.dnddl.plugin.genshin.api.ApiAccessDeniedException
import icu.dnddl.plugin.genshin.api.genshin.GenshinBBSApi
import icu.dnddl.plugin.genshin.database.getUserData
import icu.dnddl.plugin.genshin.draw.infoImage
import icu.dnddl.plugin.genshin.model.GachaSimulator
import icu.dnddl.plugin.genshin.service.AbstractCactusService
import icu.dnddl.plugin.genshin.util.requireCookie
import kotlinx.serialization.SerializationException
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.utils.ExternalResource.Companion.uploadAsImage
import org.jetbrains.skia.EncodedImageFormat
import org.laolittle.plugin.sendImage
import org.laolittle.plugin.toExternalResource

object GroupMessageListener : AbstractCactusService() {
    private val users = mutableSetOf<Long>()
    override suspend fun main() {
        globalEventChannel().subscribeGroupMessages {
            (startsWith("原神") or startsWith(CactusConfig.botName)) Listener@{ foo ->
                val result = Regex("""(人物|十连|单抽|查询)(.*)""").find(foo)?.groupValues
                when (result?.get(1)) {
                    "十连" -> {
                        if (!users.add(sender.id)) return@Listener
                        val entities = GachaSimulator.gachaCharacter(sender.id, 1, 10)
                        GachaSimulator.renderGachaImage(entities).toExternalResource(EncodedImageFormat.JPEG)
                            .use { ex ->
                                subject.sendMessage(ex.uploadAsImage(subject) + message.quote())
                            }
                        users.remove(sender.id)
                    }
                    "查询" -> {
                        val userData = if (CactusConfig.allowAnonymous) getUserData(sender.id)
                        else sender.requireCookie { return@Listener }

                        val cookies = userData.data.cookies.takeIf { it.isNotBlank() } ?: CactusData.cookie

                        val uid = result[2].replace(Regex("""[\s]+"""), "").toLongOrNull()
                        if (uid == null || uid < 100000100 || uid > 700000000) {
                            subject.sendMessage("请输入正确的uid")
                            return@Listener
                        }
                        val query = kotlin.runCatching {
                            GenshinBBSApi.getPlayerInfo(uid, cookies, userData.data.uuid)
                        }.getOrElse {
                            when (it) {
                                is SerializationException -> subject.sendMessage("请求失败！请检查uid是否正确")
                                is ApiAccessDeniedException -> subject.sendMessage("获取失败: ${it.message}")
                                else -> CactusBot.logger.error(it)
                            }
                            return@Listener
                        }

                        subject.sendImage(query.infoImage())
                    }
                }
            }

            finding(Regex("原神登[录陆]")) {
                subject.sendMessage("请加好友私聊发送")
            }
        }
    }
}