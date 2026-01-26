package io.github.seggan.glagolitsa.ui

import androidx.compose.ui.graphics.Color
import com.composeunstyled.theme.ThemeProperty
import com.composeunstyled.theme.ThemeToken
import com.composeunstyled.theme.buildTheme

val colors = ThemeProperty<Color>("colors")
val background = ThemeToken<Color>("background")
val onBackground = ThemeToken<Color>("on_background")
val outline = ThemeToken<Color>("outline")

val LightTheme = buildTheme {
    defaultContentColor = Color(0xFF000000)
    properties[colors] = mapOf(
        background to Color(0xFFFFFFFF),
        onBackground to defaultContentColor,
        outline to Color(0xFF343434),
    )
}