package com.imr.reqestra

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MainActivityTest {
    @get:Rule val composeRule = createAndroidComposeRule<MainActivity>()

    @Test fun headerActionsRemainVisibleAndSeparate() {
        val language = composeRule.onNodeWithTag("action-language")
        val donation = composeRule.onNodeWithTag("action-donate")
        val theme = composeRule.onNodeWithTag("action-theme")

        language.assertIsDisplayed()
        donation.assertIsDisplayed()
        theme.assertIsDisplayed()

        val languageBounds = language.fetchSemanticsNode().boundsInRoot
        val donationBounds = donation.fetchSemanticsNode().boundsInRoot
        val themeBounds = theme.fetchSemanticsNode().boundsInRoot
        assertTrue("Language and donation overlap", languageBounds.right <= donationBounds.left)
        assertTrue("Donation and theme overlap", donationBounds.right <= themeBounds.left)
    }

    @Test fun languageMenuCanBeOpenedFromHeader() {
        composeRule.onNodeWithTag("action-language").performClick()
        composeRule.onNodeWithText("Español").assertExists()
    }

    @Test fun themeControlRemainsAvailableAfterChange() {
        composeRule.onNodeWithTag("action-theme").performClick()
        composeRule.onNodeWithTag("action-theme").assertIsDisplayed()
    }

    @Test fun donationOpensTheSupportSection() {
        composeRule.onNodeWithTag("action-donate").performClick()
        composeRule.onNodeWithTag("action-donate").assertIsDisplayed()
    }
}
