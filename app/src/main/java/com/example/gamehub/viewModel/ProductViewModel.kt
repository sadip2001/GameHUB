package com.example.gamehub.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gamehub.model.ProductModel
import com.example.gamehub.repository.ProductRepo

class ProductViewModel(val repo: ProductRepo) : ViewModel() {

    private val _products = MutableLiveData<List<ProductModel>?>()
    val products: MutableLiveData<List<ProductModel>?> get() = _products

    private val _product = MutableLiveData<ProductModel?>()
    val product: MutableLiveData<ProductModel?> get() = _product

    fun addProduct(model: ProductModel, callback: (Boolean, String) -> Unit) {
        repo.addProduct(model, callback)
    }

    fun updateProduct(productId: String, model: ProductModel, callback: (Boolean, String) -> Unit) {
        repo.updateProduct(productId, model, callback)
    }

    fun deleteProduct(productId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteProduct(productId, callback)
    }

    fun getAllProducts() {
        repo.getAllProducts { success, message, list ->
            if (success) _products.postValue(list)
        }
    }
}