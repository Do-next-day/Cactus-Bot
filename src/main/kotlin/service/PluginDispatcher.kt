package icu.dnddl.plugin.genshin.service

import icu.dnddl.plugin.genshin.CactusBot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

object PluginDispatcher : CoroutineScope {
    override val coroutineContext: CoroutineContext get() = SupervisorJob(CactusBot.coroutineContext.job)

    fun <T> runBlocking(block: suspend CoroutineScope.() -> T): T =
        runBlocking(coroutineContext, block)

}