package com.example.gamehub.repository

import com.example.gamehub.model.ProductModel

interface ProductRepo {
    fun addProduct(model: ProductModel, callback: (Boolean, String) -> Unit)
    fun updateProduct(productId: String, model: ProductModel, callback: (Boolean, String) -> Unit)
    fun deleteProduct(productId: String, callback: (Boolean, String) -> Unit)
    fun getProductById(productId: String, callback: (Boolean, String, ProductModel?) -> Unit)
    fun getAllProducts(callback: (Boolean, String, List<ProductModel>?) -> Unit)
}