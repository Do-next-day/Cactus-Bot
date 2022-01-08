package org.laolittle.plugin

import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel

object EventListener {
    fun listeningOnGroup(groupId: Long? = null, block: suspend GroupMessageEvent.(GroupMessageEvent) -> Unit) =
        GenshinHelper.globalEventChannel()
            .filterIsInstance<GroupMessageEvent>()
            .filter { it.subject.id == groupId }
            .subscribeAlways(handler = block)
}