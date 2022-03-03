package org.laolittle.plugin.genshin.service

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
    serviceName: String
) : AbstractCactusService(ctx, Type.Task, serviceName) {
    constructor(
        ctx: CoroutineContext? = null,
    ) : this(ctx, "Unknown Timer Service") {
        this.serviceName = this::class.simpleName ?: return
    }

    fun start(delay: Long): Boolean {
        return if (job?.isActive != true) {
            job = launch(coroutineContext) {
                while (this.isActive)
                    try {
                        main()
                    } catch (e: Throwable) {
                        logger.error(e)
                    } finally {
                        delay(delay)
                    }
            }
            CactusBot.logger.info { "Service: $serviceName started successfully" }
            true
        } else false
    }

    fun startAt(time: LocalDateTime, delay: Long = 0) {
        launch {
            CactusBot.logger.info { "Service: $serviceName will start at $time" }
            val remain = time.toInstant(TimeZone.of("+8")).toEpochMilliseconds() - currentTimeMillis
            delay(remain)
            start(delay)
        }
    }
}