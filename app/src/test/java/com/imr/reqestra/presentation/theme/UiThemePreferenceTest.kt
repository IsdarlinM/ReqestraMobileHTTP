package com.imr.reqestra.presentation.theme

import org.junit.Assert.assertEquals
import org.junit.Test

class UiThemePreferenceTest {
    @Test fun defaultsToSystemUntilUserChooses() {
        assertEquals(true, UiThemePreference.isDark(UiThemePreference.SYSTEM, true))
        assertEquals(false, UiThemePreference.isDark(null, false))
    }

    @Test fun aManualSelectionOverridesDeviceTheme() {
        assertEquals(false, UiThemePreference.isDark(UiThemePreference.LIGHT, true))
        assertEquals(true, UiThemePreference.isDark(UiThemePreference.DARK, false))
    }

    @Test fun toggleAlwaysProducesOppositeAppearance() {
        assertEquals(UiThemePreference.LIGHT, UiThemePreference.toggled(true))
        assertEquals(UiThemePreference.DARK, UiThemePreference.toggled(false))
    }
}
