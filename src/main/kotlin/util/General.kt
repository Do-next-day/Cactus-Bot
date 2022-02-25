package org.laolittle.plugin.genshin.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.laolittle.plugin.genshin.GenshinHelper
import kotlin.coroutines.CoroutineContext

fun <T> launchBlocking(block: suspend CoroutineScope.() -> T): T = runBlocking(GenshinHelper.coroutineContext, block)