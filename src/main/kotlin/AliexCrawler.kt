import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.ElementNotFoundException
import it.skrape.selects.html5.span
import java.io.File
import java.io.FileReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class AliexCrawler {

    private fun crawPrice(productUrl: String): String {
        var priceRange = ""

        try {
            skrape(BrowserFetcher) {
                request {
                    url = productUrl
                    userAgent =
                        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36"
                    sslRelaxed = true
                    timeout = 10000
                }
                response {
//                    println(responseBody)
                    priceRange = htmlDocument {
                        span(".product-price-value") {
                            findFirst {
                                text
                            }
                        }
                    }
                }
            }
        } catch (e: ElementNotFoundException) {
//            try {
//                skrape(BrowserFetcher) {
//                    request {
//                        url = productUrl
//                        userAgent =
//                            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36"
//                        sslRelaxed = true
//                        timeout = 10000
//                    }
//                    response {
//                        println(responseBody)
//                        priceRange = htmlDocument {
//                            span(".uniform-banner-box-discounts > span") {
//                                findFirst {
//                                    text
//                                }
//                            }
//                        }
//                    }
//                }
//            } catch (e: ElementNotFoundException) {
                println("Get price range error for product $productUrl")
                return ""
//            }
        }

        var maxPrice = ""
        val p = Pattern.compile("\\d+$")
        val matcher = p.matcher(priceRange)
        if (matcher.find()) {
            maxPrice = matcher.group()
        }

        return maxPrice
    }

    private fun calculateSellPrice(basePrice: Int): String {
        return kotlin.math.ceil(basePrice * 1.036 + 40 + 500).toInt().toString()
    }

    fun checkPriceWithProductSource() {
        val logFile = File(
            "src/main/resources/price_check_log/" +
                    "${LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}.txt")


        val csvReader = CSVReaderBuilder(FileReader("src/main/resources/product.csv"))
            .withCSVParser(CSVParserBuilder().withSeparator(',').build())
            .build()

        // val header = csvReader.readNext()

        var line: Array<String>? = csvReader.readNext()
        while (line != null) {
//            println(line[0])
//            println(line[1])
//            println(line[2])
//            println(line[3])
            val crawledPrice = crawPrice(line[3])
            if (crawledPrice == "") {
                logFile.appendText(
                    "Cannot get price for product ${line[0]}, productID ${line[1]}, current price ${line[2]}\n"
                )
                line = csvReader.readNext()
                continue
            }
            val sellPrice = calculateSellPrice(crawledPrice.toInt())
            if (sellPrice != line[2]) {
                logFile.appendText(
                    "Price not match for product ${line[0]}, productID ${line[1]}, " +
                            "current price ${line[2]}, crawled price $sellPrice\n"
                )
            }
            line = csvReader.readNext()
        }
    }
}
