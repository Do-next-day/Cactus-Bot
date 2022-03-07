package icu.dnddl.plugin.genshin.mirai

import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.AudioSupported
import net.mamoe.mirai.event.events.MessageEvent

inline fun <E : MessageEvent> E.messageContext(block: MessageContext.() -> Unit) {
    MessageContext(this).block()
}

fun getSubjectFromBots(id: Long): AudioSupported? {
    for (bot in Bot.instances)
        if (bot.isOnline)
            return bot.getFriend(id) ?: bot.getGroup(id) ?: continue
    return null
}