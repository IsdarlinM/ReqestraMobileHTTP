package com.imr.reqestra.presentation.theme

/** Local UI-only preference; does not affect API requests or other app settings. */
object UiThemePreference {
    const val KEY = "theme_mode"
    const val SYSTEM = "system"
    const val LIGHT = "light"
    const val DARK = "dark"

    fun isDark(mode: String?, systemDark: Boolean): Boolean = when (mode) {
        DARK -> true
        LIGHT -> false
        else -> systemDark
    }

    fun toggled(currentlyDark: Boolean): String = if (currentlyDark) LIGHT else DARK
}
