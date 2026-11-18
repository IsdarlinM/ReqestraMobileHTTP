package com.imr.reqestra.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val DarkScheme = darkColorScheme(
    primary = StudioTeal,
    onPrimary = Color(0xFF07332B),
    primaryContainer = Color(0xFF11493F),
    onPrimaryContainer = Color(0xFFB6F8E6),
    secondary = StudioSky,
    onSecondary = Color(0xFF102F49),
    secondaryContainer = Color(0xFF213F57),
    onSecondaryContainer = Color(0xFFD6ECFF),
    tertiary = StudioAmber,
    onTertiary = Color(0xFF4D310B),
    tertiaryContainer = Color(0xFF544029),
    onTertiaryContainer = Color(0xFFFFE8C2),
    error = StudioError,
    onError = Color(0xFF5F1415),
    errorContainer = Color(0xFF5C2626),
    onErrorContainer = Color(0xFFFFDAD5),
    background = StudioNavy,
    onBackground = StudioOnDark,
    surface = StudioSurface,
    onSurface = StudioOnDark,
    surfaceVariant = StudioSurfaceVariant,
    onSurfaceVariant = Color(0xFFB8C8D5),
    outline = Color(0xFF829AA8),
    outlineVariant = Color(0xFF3B5362),
    inverseSurface = Color(0xFFEAF4F8),
    inverseOnSurface = StudioNavy,
    inversePrimary = StudioTealDark,
    surfaceTint = StudioTeal,
    scrim = Color.Black,
)

private val LightScheme = lightColorScheme(
    primary = StudioTealDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCBF5E8),
    onPrimaryContainer = Color(0xFF004E40),
    secondary = StudioSkyDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD5EAFA),
    onSecondaryContainer = Color(0xFF123A56),
    tertiary = StudioAmberDark,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFBE9D1),
    onTertiaryContainer = Color(0xFF5A3914),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF601410),
    background = StudioSurfaceLight,
    onBackground = StudioOnLight,
    surface = Color.White,
    onSurface = StudioOnLight,
    surfaceVariant = Color(0xFFE5EDF2),
    onSurfaceVariant = Color(0xFF506472),
    outline = Color(0xFF718895),
    outlineVariant = Color(0xFFC5D4DD),
    inverseSurface = Color(0xFF203644),
    inverseOnSurface = Color(0xFFF0F8FC),
    inversePrimary = StudioTeal,
    surfaceTint = StudioTealDark,
    scrim = Color.Black,
)

private val StudioShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun ReqestraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    // Brand palette is predictable by default; dynamic color is opt-in.
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkScheme
        else -> LightScheme
    }
    MaterialTheme(colorScheme = colorScheme, typography = StudioTypography,
        shapes = StudioShapes, content = content)
}
