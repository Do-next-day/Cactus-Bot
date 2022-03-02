package org.laolittle.plugin.genshin.service

import kotlinx.coroutines.*
import kotlinx.datetime.*
import net.mamoe.mirai.utils.info
import org.laolittle.plugin.genshin.CactusBot
import org.laolittle.plugin.genshin.util.currentTimeMillis
import kotlin.coroutines.CoroutineContext

abstract class CactusService(
    private val ctx: CoroutineContext? = null,
    private val type: Type = Type.Job,
) : CoroutineScope, CompletableJob by SupervisorJob(CactusBot.coroutineContext.job) {
    private val serviceName: String = this::class.simpleName ?: "Unknown"
    private var job: Job? = null

    final override val coroutineContext: CoroutineContext
        get() = if (ctx != null) this + ctx else this

    protected abstract suspend fun main()

    override fun start(): Boolean {
        return if (job?.isActive != true) {
            job = launch(coroutineContext) { main() }
            CactusBot.logger.info { "Service: $serviceName started successfully" }
            true
        } else false
    }

    override fun cancel(cause: CancellationException?) {
        job?.cancel(cause)
    }

    override suspend fun join() {
        if (job?.isActive != true) return
        if (type != Type.Task) job?.join()
        else throw IllegalStateException("Target is a task")
    }

    open fun startAt(time: LocalDateTime) {
        launch {
            delay(time.toInstant(TimeZone.of("+8")).toEpochMilliseconds() - currentTimeMillis)
            start()
        }
    }

    fun startAt(time: LocalDate) =
        startAt(time.toJavaLocalDate().atStartOfDay().toKotlinLocalDateTime())


    enum class Type {
        Job,
        Task
    }
}