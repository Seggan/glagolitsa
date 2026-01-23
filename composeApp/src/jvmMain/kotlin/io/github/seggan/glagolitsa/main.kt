package io.github.seggan.glagolitsa

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.seggan.glagolitsa.ui.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "glagolitsa",
    ) {
        App()
    }
}