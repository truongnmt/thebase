import com.jayway.jsonpath.JsonPath
import io.github.cdimascio.dotenv.Dotenv
import model.Category
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class ItemAdd {
    private var accessToken: String

    init {
        val dotEnv = Dotenv.load()
        accessToken = dotEnv.get("THEBASE_ACCESS_TOKEN")
    }

    fun publish(parser: Parser, category: Category) {
        // Product API
        val itemAddParams = mutableMapOf(
            "title" to URLEncoder.encode(parser.productTitle, "UTF-8"),
            "detail" to URLEncoder.encode(
                productDetail(parser.productId, parser.productTitle, parser.productVariants),
                "UTF-8"
            ),
            "price" to parser.productPrice,
            "stock" to 888,
            "visible" to 0,
            "identifier" to parser.productId,
            "list_order" to 1,
        )

        parser.productVariants.forEachIndexed { index, variantName ->
            itemAddParams["variation[${index}]"] = URLEncoder.encode(variantName, "UTF-8")
            itemAddParams["variation_stock[${index}]"] = 888
        }

        val itemAddUrlParams = itemAddParams.map{(k, v) -> "$k=$v" }.joinToString("&")
        // println(itemAddUrlParams)
        val client = HttpClient.newBuilder().build();
        var request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.thebase.in/1/items/add?${itemAddUrlParams}"))
            .POST(HttpRequest.BodyPublishers.noBody())
            .header("Authorization", "Bearer $accessToken")
            .build()
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        println(response.body())

        val docCtx = JsonPath.parse(response.body())
        val itemId: Int = docCtx.read("$.item.item_id")
        println("item_id: $itemId")

        // Category API
        val requestParams = mapOf(
            "item_id" to itemId,
            "category_id" to category.reverseCategory[parser.productType]
        ).map{(k,v) -> "$k=$v"}.joinToString("&")
        request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.thebase.in/1/item_categories/add?${requestParams}"))
            .POST(HttpRequest.BodyPublishers.noBody())
            .header("Authorization", "Bearer $accessToken")
            .build()
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        println(response.body())
    }

    fun productDetail(productId: String, productTitle: String,
                      productVariants: ArrayList<String>): String {
        return "− − − − − − − − − − − − − − − − − − − − −\n" +
                "＊ご購入いただく前にこちらをご確認ください。\n" +
                "https://techtoy.thebase.in/blog/2021/04/16/001805\n" +
                "− − − − − − − − − − − − − − − − − − − − −\n" +
                "\n" +
                "\n" +
                "＊* 商品説明 *＊\n" +
                "$productId $productTitle\n" +
                "\n" +
                "\n" +
                "＊* 対応機種 *＊\n" +
                "${productVariants.joinToString("\n・ ") }}" +
                "カメラ・ボタン位置などは、それぞれの機種に合わせてつくられていますのでご安心ください。\n" +
                "\n" +
                "\n" +
                "＊* 注意事項 *＊\n" +
                "techtoy で販売している商品は全品、海外インポート品になります。\n" +
                "全品漏れなく検品し、使用上問題無いと判断したものを出品しておりますのでご安心ください。\n" +
                " \n" +
                "\n" +
                "＊*ショップ・商品のレビューについて*＊\n" +
                "お客さん 100%の満足 は当店のターゲットです。お客さんの満足はサービスの原則です。\n" +
                "ショップ・商品のレビューは当店のビジネスにとって非常に重要です。\n" +
                "従って当店の商品に満足したらぜひ【:D】レビューいただきたいです。 \n" +
                "\n" +
                "\n" +
                "※全国一律送料無料（送料無料オプション）"
    }
}
