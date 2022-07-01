import model.Category

fun main(args: Array<String>) {
//    var aliexCrawler = AliexCrawler("4001328236329")
//    aliexCrawler.crawl()

    val productId = "A1091"
    val parser = Parser(productId)
    parser.parse()
    // parser.print()

    val category = Category()

    val itemAdd = ItemAdd()
    itemAdd.publish(parser, category)

}
