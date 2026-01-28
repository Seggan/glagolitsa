package io.github.seggan.glagolitsa.ui

import androidx.compose.ui.graphics.Color
import com.composeunstyled.theme.ThemeProperty
import com.composeunstyled.theme.ThemeToken
import com.composeunstyled.theme.buildTheme
import com.composeunstyled.theme.rememberColoredIndication

val colors = ThemeProperty<Color>("colors")

val background = ThemeToken<Color>("background")
val onBackground = ThemeToken<Color>("on_background")

val surface = ThemeToken<Color>("surface")
val onSurface = ThemeToken<Color>("on_surface")

val primary = ThemeToken<Color>("primary")
val onPrimary = ThemeToken<Color>("on_primary")

val outline = ThemeToken<Color>("outline")

val LightTheme = buildTheme {
    defaultContentColor = Color(0xFFE6E6E6)

    defaultIndication = rememberColoredIndication(
        hoveredColor = Color.Black.copy(alpha = 0.2f),
        pressedColor = Color.White.copy(alpha = 0.5f),
        focusedColor = Color.Black.copy(alpha = 0.1f),
    )

    properties[colors] = mapOf(
        background to Color(0xFF1E1E1E),
        onBackground to defaultContentColor,
        surface to Color(0xFF2A2A2A),
        onSurface to defaultContentColor,
        primary to Color(0xFF4F00C6),
        onPrimary to Color.White,
        outline to Color(0xFF3A3A3A),
    )
}