import icu.dnddl.plugin.genshin.draw.makeDynamicImage
import kotlinx.coroutines.runBlocking
import org.jetbrains.skia.*
import org.junit.Test
import java.awt.SystemColor.text

internal class ApiTest {

    @Test
    fun biliDynamicTest():Unit = runBlocking{
//        println(BiliBiliApi.getNewDynamic(401742377))

//        val f = Font(Typeface.makeFromName("Noto Sans SC", FontStyle.NORMAL), 35f)
//        val t = TextLine.make("ssssss", f)
        makeDynamicImage()

    }

}