package org.laolittle.plugin.genshin.service

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import net.mamoe.mirai.utils.info
import org.laolittle.plugin.genshin.CactusBot
import org.laolittle.plugin.genshin.api.internal.logger
import org.laolittle.plugin.genshin.util.currentTimeMillis
import kotlin.coroutines.CoroutineContext

abstract class AbstractCactusTimerService(
    ctx: CoroutineContext? = null,
) : AbstractCactusService(ctx, Type.Task) {
    constructor(
        ctx: CoroutineContext? = null,
        serviceName: String
    ) : this(ctx) {
        this.serviceName = serviceName
    }

    private var serviceName: String = this::class.simpleName ?: "Unknown"
    private var job: Job? = null

    override fun start(): Boolean {
        return if (job?.isActive != true) {
            job = launch(coroutineContext) {
                while (this.isActive)
                    try {
                        main()
                    } catch (e: Throwable) {
                        logger.error(e)
                    }
            }
            CactusBot.logger.info { "Service: $serviceName started successfully" }
            true
        } else false
    }

    fun start(time: LocalDateTime) {
        launch {
            CactusBot.logger.info { "Service: $serviceName will start at $time" }
            val remain = time.toInstant(TimeZone.of("+8")).toEpochMilliseconds() - currentTimeMillis
            delay(remain)
            start()
        }
    }
}