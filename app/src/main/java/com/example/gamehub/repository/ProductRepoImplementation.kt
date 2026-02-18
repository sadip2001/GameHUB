package com.example.gamehub.repository

import com.example.gamehub.model.ProductModel
import com.google.firebase.database.*

class ProductRepoImplementation : ProductRepo {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("Products")

    override fun addProduct(model: ProductModel, callback: (Boolean, String) -> Unit) {
        val id = ref.push().key ?: ""
        model.id = id
        ref.child(id).setValue(model).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Product added successfully")
            else callback(false, "${it.exception?.message}")
        }
    }

    override fun updateProduct(productId: String, model: ProductModel, callback: (Boolean, String) -> Unit) {
        ref.child(productId).updateChildren(model.toMap()).addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Product updated")
            else callback(false, "${it.exception?.message}")
        }
    }

    override fun deleteProduct(productId: String, callback: (Boolean, String) -> Unit) {
        ref.child(productId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) callback(true, "Product deleted")
            else callback(false, "${it.exception?.message}")
        }
    }

    override fun getProductById(productId: String, callback: (Boolean, String, ProductModel?) -> Unit) {
        ref.child(productId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val product = snapshot.getValue(ProductModel::class.java)
                callback(true, "Product fetched", product)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun getAllProducts(callback: (Boolean, String, List<ProductModel>?) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = mutableListOf<ProductModel>()
                for (data in snapshot.children) {
                    val product = data.getValue(ProductModel::class.java)
                    product?.let { products.add(it) }
                }
                callback(true, "Products fetched", products)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }
}