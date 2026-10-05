package de.welpentraining.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light = lightColorScheme(
    primary = Color(0xFF9A4A0B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDCC4),
    onPrimaryContainer = Color(0xFF331200),
    secondary = Color(0xFF6F5B40),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF9DEBB),
    onSecondaryContainer = Color(0xFF261904),
    tertiary = Color(0xFF4F6638),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1ECB2),
    onTertiaryContainer = Color(0xFF0E2000),
    background = Color(0xFFFFF8F4),
    onBackground = Color(0xFF221A15),
    surface = Color(0xFFFFF8F4),
    onSurface = Color(0xFF221A15),
    surfaceVariant = Color(0xFFF4DED2),
    onSurfaceVariant = Color(0xFF52443B),
    outline = Color(0xFF85746A),
)

private val Dark = darkColorScheme(
    primary = Color(0xFFFFB782),
    onPrimary = Color(0xFF552100),
    primaryContainer = Color(0xFF783200),
    onPrimaryContainer = Color(0xFFFFDCC4),
    secondary = Color(0xFFDCC2A1),
    onSecondary = Color(0xFF3E2D16),
    secondaryContainer = Color(0xFF56432A),
    onSecondaryContainer = Color(0xFFF9DEBB),
    tertiary = Color(0xFFB5D098),
    onTertiary = Color(0xFF22370E),
    tertiaryContainer = Color(0xFF384E23),
    onTertiaryContainer = Color(0xFFD1ECB2),
    background = Color(0xFF1A120D),
    onBackground = Color(0xFFF0DFD6),
    surface = Color(0xFF1A120D),
    onSurface = Color(0xFFF0DFD6),
    surfaceVariant = Color(0xFF52443B),
    onSurfaceVariant = Color(0xFFD7C3B7),
    outline = Color(0xFF9F8D83),
)

@Composable
fun WelpenTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Dark else Light, content = content)
}
