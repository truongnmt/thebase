package model

class Category() {
    val reverseCategory = mapOf(
        Type.IPHONE to 3364531,
        Type.AIRPODS_PRO to 3364561,
        Type.AIRPODS to 3364562,
            )

    enum class Type {
        IPHONE, AIRPODS_PRO, AIRPODS
    }
}
