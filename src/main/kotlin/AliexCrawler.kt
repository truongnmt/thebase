import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.eachImage
import it.skrape.selects.eachSrc
import it.skrape.selects.eachText
import it.skrape.selects.html
import it.skrape.selects.html5.div
import it.skrape.selects.html5.h1
import model.AliexProduct
import model.AliexVariationImage
import model.AliexVariationText

class AliexCrawler(var productId: String) {

    fun crawl() {
        val extracted = skrape(BrowserFetcher) {
            request {
                url = "https://ja.aliexpress.com/item/${productId}.html"
                userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.0.0 Safari/537.36"
                sslRelaxed = true
                timeout = 10000
            }
            response {
                println(responseBody)
                println(htmlDocument {
                    div(".sku-property-image") {
                        findAll("img") {
                            html
                        }
                    }
                })
//                val aliexVariationText: List<AliexVariationText> = htmlDocument {
//                    div(".sku-property-text") { findAll { eachText } }
//                }.map {
//                    it.replace("for ", "")
//                    AliexVariationText(name = it)
//                }
//
////                val aliexVariationImage: List<AliexVariationImage> = htmlDocument {
////                    div(".sku-property-image") { findAll { eachSrc } }
////                }.map {
//                htmlDocument {
//                    div(".sku-property-image") { println(it) }
//                }
////                    .map {
////                    // input:  ...asdfQuicksand.jpg_50x50.jpg_.webp
////                    // output: ...asdfQuicksand.jpg_640x640.jpg
////                    println(it)
////                    it.replace("_50x50", "_640x640")
////                    it.replace("_.webp", "")
////                    AliexVariationImage(img = it)
////                }
//
////                aliexVariationImage.listIterator()
//
//                val aliexProduct = AliexProduct(
//                    id = productId,
//                    title = htmlDocument {
//                        h1(".product-title-text") { findFirst { text } }
//                    },
//                    variationText = aliexVariationText,
////                    variationImage = aliexVariationImage,
//                )
//                aliexProduct

            }
        }
//        print(extracted)
    }
}

//https://ae01.alicdn.com/kf/H3e48b342e1cd44118b6284ef59652c2br/Luxury-Gold-Foil-Silicone-Case-For-iPhone-13-12-11-Pro-Xs-Max-SE-Glitter-Quicksand.jpg_50x50.jpg_.webp
//https://ae01.alicdn.com/kf/H3e48b342e1cd44118b6284ef59652c2br/Luxury-Gold-Foil-Silicone-Case-For-iPhone-13-12-11-Pro-Xs-Max-SE-Glitter-Quicksand.jpg_640x640.jpg
