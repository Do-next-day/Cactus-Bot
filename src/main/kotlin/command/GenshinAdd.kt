package org.laolittle.plugin.genshin.command

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import org.jetbrains.exposed.sql.transactions.transaction
import org.laolittle.plugin.genshin.GenshinHelper
import org.laolittle.plugin.genshin.database.Character
import java.time.LocalDate

object GenshinAdd : SimpleCommand(
    GenshinHelper, "gadd",
) {
    @OptIn(ExperimentalCommandDescriptors::class, ConsoleExperimentalApi::class)
    override val prefixOptional: Boolean = true

    @Handler
    suspend fun CommandSender.handle(nameOri: String, isStar: Boolean, dateStr: String) {
        val dateList = dateStr.split("/")
        val dateDate = runCatching {
            LocalDate.of(dateList[0].toInt(), dateList[1].toInt(), dateList[2].toInt())
        }.getOrElse {
            sendMessage("请输入正确的时间！")
            return
        }
        transaction(GenshinHelper.db) {
            Character.new {
                name = nameOri
                star = isStar
                date = dateDate
                description = "描述"
            }
        }
        sendMessage("成功添加")
    }
}