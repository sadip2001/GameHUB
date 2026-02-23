package com.example.gamehub

import com.example.gamehub.model.ProductModel
import com.example.gamehub.repository.ProductRepo
import com.example.gamehub.viewModel.ProductViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class ProductViewModelTest {

    @Test
    fun addProduct_success_test() {
        val repo = mock<ProductRepo>()
        val viewModel = ProductViewModel(repo)
        val product = ProductModel(title = "Football")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Product added")
            null
        }.`when`(repo).addProduct(any(), any())

        var successResult = false
        viewModel.addProduct(product) { success, _ ->
            successResult = success
        }

        assertTrue(successResult)
        verify(repo).addProduct(any(), any())
    }

    @Test
    fun deleteProduct_success_test() {
        val repo = mock<ProductRepo>()
        val viewModel = ProductViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Product deleted")
            null
        }.`when`(repo).deleteProduct(eq("prod_123"), any())

        var messageResult = ""
        viewModel.deleteProduct("prod_123") { _, msg ->
            messageResult = msg
        }

        assertEquals("Product deleted", messageResult)
        verify(repo).deleteProduct(eq("prod_123"), any())
    }

    @Test
    fun updateProduct_success_test() {
        val repo = mock<ProductRepo>()
        val viewModel = ProductViewModel(repo)
        val product = ProductModel(title = "Updated Bat")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Update successful")
            null
        }.`when`(repo).updateProduct(eq("prod_123"), any(), any())

        var successResult = false
        viewModel.updateProduct("prod_123", product) { success, _ ->
            successResult = success
        }

        assertTrue(successResult)
        verify(repo).updateProduct(eq("prod_123"), any(), any())
    }
}