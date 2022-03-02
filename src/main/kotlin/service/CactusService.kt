package org.laolittle.plugin.genshin.service

import kotlinx.coroutines.*
import org.laolittle.plugin.genshin.CactusBot
import kotlin.coroutines.CoroutineContext

abstract class CactusService(private val ctx: CoroutineContext? = null, private val type: Type = Type.Job) :
    CoroutineScope, CompletableJob by SupervisorJob(CactusBot.coroutineContext.job) {
    private var job: Job? = null

    final override val coroutineContext: CoroutineContext
        get() = if (ctx != null) this.plus(ctx) else this

    protected abstract suspend fun main()

    override fun start(): Boolean {
        return if (job?.isActive != true) {
            job = launch(coroutineContext) { main() }
            true
        } else false
    }

    override fun cancel(cause: CancellationException?) {
        job?.cancel(cause)
    }

    override suspend fun join() {
        if (type != Type.Task) {
            job?.join()
        } else throw IllegalStateException("Target is a task")
    }

    enum class Type {
        Job,
        Task
    }
}