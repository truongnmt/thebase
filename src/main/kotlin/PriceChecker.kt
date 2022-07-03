import org.apache.commons.lang3.ClassLoaderUtils
import java.io.File

fun main(args: Array<String>) {
    val aliexCrawler = AliexCrawler()
    aliexCrawler.checkPriceWithProductSource()
}
