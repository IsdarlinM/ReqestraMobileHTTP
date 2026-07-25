package com.imr.reqestra

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class MainActivityTest {
    @get:Rule val composeRule = createAndroidComposeRule<MainActivity>()

    @Test fun appStartsAndShowsRequestBuilder() {
        composeRule.onNodeWithText("Reqestra").assertIsDisplayed()
        composeRule.onNodeWithText("Constructor de petición").assertIsDisplayed()
    }
}
