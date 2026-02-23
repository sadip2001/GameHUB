package com.example.gamehub

import com.example.gamehub.model.UserModel
import com.example.gamehub.repository.UserRepo
import com.example.gamehub.viewModel.UserViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class UserViewModelTest {

    @Test
    fun register_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, String) -> Unit>(2)
            callback(true, "Registration success", "user_123")
            null
        }.`when`(repo).register(eq("new@gmail.com"), eq("password"), any())

        var successResult = false
        var uidResult = ""

        viewModel.register("new@gmail.com", "password") { success, _, uid ->
            successResult = success
            uidResult = uid
        }

        assertTrue(successResult)
        assertEquals("user_123", uidResult)
        verify(repo).register(eq("new@gmail.com"), eq("password"), any())
    }

    @Test
    fun forgetPassword_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Reset link sent")
            null
        }.`when`(repo).forgetPassword(eq("test@gmail.com"), any())

        var successResult = false
        var messageResult = ""

        viewModel.forgetPassword("test@gmail.com") { success, msg ->
            successResult = success
            messageResult = msg
        }

        assertTrue(successResult)
        assertEquals("Reset link sent", messageResult)
        verify(repo).forgetPassword(eq("test@gmail.com"), any())
    }

    @Test
    fun editProfile_success_test() {
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)
        val updatedUser = UserModel(fullName = "Sadip Silwal")

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Profile updated")
            null
        }.`when`(repo).editProfile(eq("user_123"), any(), any())

        var successResult = false
        viewModel.editProfile("user_123", updatedUser) { success, _ ->
            successResult = success
        }

        assertTrue(successResult)
        verify(repo).editProfile(eq("user_123"), any(), any())
    }
}