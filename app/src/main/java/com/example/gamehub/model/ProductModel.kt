package com.example.gamehub.model

data class ProductModel(
    var id: String = "",
    var owner: String = "",
    var title: String = "",
    var description: String = "",
    var sport: String = "",
    var category: String = "",
    var price: String = "",
    var quantity: String = "",
    var imageUrl: String = ""
) {
    fun toMap(): MutableMap<String, Any> {
        return mutableMapOf(
            "id" to id,
            "owner" to owner,
            "title" to title,
            "description" to description,
            "sport" to sport,
            "category" to category,
            "price" to price,
            "quantity" to quantity,
            "imageUrl" to imageUrl
        )
    }
}