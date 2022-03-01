package org.laolittle.plugin.genshin.service

import kotlinx.coroutines.*
import org.laolittle.plugin.genshin.CactusBot
import kotlin.coroutines.CoroutineContext

abstract class CactusService(ctx: CoroutineContext? = null) : CoroutineScope {

    final override val coroutineContext: CoroutineContext
        get() = SupervisorJob(CactusBot.coroutineContext.job)

    protected abstract suspend fun main()

    fun start() = launch(context = this.coroutineContext) { main() }

    init {
        if (ctx != null) {
            coroutineContext.plus(ctx)
        }
    }
}