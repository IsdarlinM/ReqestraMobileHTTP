package com.imr.reqestra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import com.imr.reqestra.presentation.ReqestraApp
import com.imr.reqestra.presentation.theme.ReqestraTheme
import com.imr.reqestra.presentation.theme.UiThemePreference

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val preferences = remember {
                getSharedPreferences("reqestra_ui", MODE_PRIVATE)
            }
            // Follow system appearance until the user explicitly chooses a theme.
            var chosenMode by rememberSaveable {
                mutableStateOf(preferences.getString(UiThemePreference.KEY, UiThemePreference.SYSTEM)
                    ?: UiThemePreference.SYSTEM)
            }
            val darkTheme = UiThemePreference.isDark(chosenMode, isSystemInDarkTheme())

            // Keep status/navigation bar icons legible when changing modes in-app.
            SideEffect {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }

            ReqestraTheme(darkTheme = darkTheme) {
                ReqestraApp(
                    darkTheme = darkTheme,
                    onToggleTheme = {
                        val next = UiThemePreference.toggled(darkTheme)
                        chosenMode = next
                        preferences.edit().putString(UiThemePreference.KEY, next).apply()
                    },
                )
            }
        }
    }
}
