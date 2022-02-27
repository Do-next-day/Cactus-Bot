package org.laolittle.plugin.genshin.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import org.laolittle.plugin.genshin.GenshinHelper
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

object PluginDispatcher {
    private val dispatcherDispatcher = EmptyCoroutineContext

    val coroutineContext: CoroutineContext get() = SupervisorJob(GenshinHelper.coroutineContext.job) + dispatcherDispatcher

    fun <T> runBlocking(block: suspend CoroutineScope.() -> T): T =
        kotlinx.coroutines.runBlocking(coroutineContext, block)

}