import icu.dnddl.plugin.genshin.api.bilibili.BiliBiliApi
import icu.dnddl.plugin.genshin.api.internal.BiliBiliResponse
import icu.dnddl.plugin.genshin.api.internal.client
import io.ktor.client.features.*
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import org.junit.Test

internal class ApiTest {

    @Test
    fun biliDynamicTest():Unit = runBlocking{
        println(BiliBiliApi.getNewDynamic(401742377))
    }
}