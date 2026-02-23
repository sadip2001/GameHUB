package com.example.gamehub.view

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<LoginActivity>()

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun goto_forgotPassword() {


        composeTestRule.onNodeWithTag("ForgotPassword")
            .performClick()

        composeTestRule.waitForIdle()

        intended(hasComponent(ForgotPasswordActivity::class.java.name))
    }

    fun goto_RegisterPage() {


        composeTestRule.onNodeWithTag("Register")
            .performClick()

        composeTestRule.waitForIdle()

        intended(hasComponent(RegisterActivity::class.java.name))
    }
}