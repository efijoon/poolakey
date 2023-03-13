package ir.cafebazaar.poolakey.entity

import org.json.JSONObject

class SkuDetails private constructor(
    val sku: String,
    val type: String,
    val price: String,
    val title: String,
    val description: String
) {

    override fun toString(): String {
        return """
            sku = $sku
            type = $type
            price = $price
            title = $title
            description = $description
        """.trimIndent()
    }

    companion object {
        internal fun fromJson(json: String): SkuDetails {
            val jsonObject = JSONObject(json)
            return with(jsonObject) {
                SkuDetails(
                    optString("productId"),
                    optString("type"),
                    optString("price"),
                    optString("title"),
                    optString("description"),
                )
            }
        }
    }
}