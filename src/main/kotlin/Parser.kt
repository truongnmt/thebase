import model.Category
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class Parser(val productId: String) {

    val variants: MutableMap<Int, String> = mutableMapOf()
    val mainImg = arrayListOf<String>()
    var videoUrl = ""
    var productVariants = arrayListOf<String>()
    var productTitle = ""
    var productPrice = ""
    var productType = Category.Type.IPHONE

    private val baseUrl = "/Users/stronglong/home/business/techtoy/"

    private fun stampStyleNumberToImage(styleNo: Int, imageName: String): String {
        val horizontalSpacing = 24
        val verticalSpacing = 36
        val image: BufferedImage = ImageIO.read(File("${baseUrl}${productId}/${imageName}"))
        val g: Graphics = image.graphics

        g.font = Font("Helvetica", 0, 70)
        g.color = Color(241, 151, 55)
        val styleNumberToString = "M${styleNo.toString().padStart(2, '0')}"
        g.drawString(styleNumberToString, horizontalSpacing, image.height - verticalSpacing)
        g.dispose()
        val newImageName = "${styleNumberToString}${imageName}".replace("variants", "")
        ImageIO.write(image, "jpg", File("${baseUrl}${productId}/${newImageName}"))

        return newImageName
    }

    fun parse() {
        var countVariant = 1
        File("${baseUrl}${productId}/").walk().maxDepth(1).forEach {
            if ("_variants-" in it.name) {
                variants[countVariant] = stampStyleNumberToImage(countVariant, it.name)
                countVariant += 1
            }
            if ("_main-" in it.name) {
                mainImg.add(it.name)
            }
            if (".mp4" in it.name) {
                videoUrl = it.name
            }
        }

//        println("Variants:")
//        for((k, v) in variants) {
//            println("Variant ${k}: ${v}")
//        }
//        println("\nMain img:")
//        mainImg.forEach{ println(it) }
//        if (videoUrl != "") println("\nVideo url:\n${videoUrl}")

        val dataTxtUrl = "${baseUrl}${productId}/data.txt"
        var countLine = 1
        File(dataTxtUrl).forEachLine {
            when (countLine) {
                1 -> productTitle = "$productId $it"
                2 -> productPrice = it
                else -> {
                    productVariants.add(it.replace("For iphone", "iPhone"))
                    if (it.contains("airpods pro", ignoreCase = true) ||
                            it.contains("airpods 3", ignoreCase = true)) {
                        productType = Category.Type.AIRPODS_PRO
                    } else if (it.contains("airpods", ignoreCase = true)) {
                        productType = Category.Type.AIRPODS
                    }
                }
            }
            countLine++
        }
//        println("\nProduct variants text:")
//        productVariants.forEach { println(it) }
    }

    fun print() {
        println("Variants:")
        for((k, v) in variants) {
            println("Variant ${k}: ${v}")
        }
        println("\nMain img:")
        mainImg.forEach{ println(it) }
        if (videoUrl != "") println("\nVideo url:\n${videoUrl}")

        println("\nProduct variants text:")
        productVariants.forEach { println(it) }
    }
}