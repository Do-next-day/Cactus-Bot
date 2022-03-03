package org.laolittle.plugin.genshin.mirai

import net.mamoe.mirai.event.events.MessageEvent

inline fun <E : MessageEvent> E.messageContext(block: MessageContext.() -> Unit) {
    MessageContext(this).block()
}