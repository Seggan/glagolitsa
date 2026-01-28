package io.github.seggan.glagolitsa.node

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.foundation.Image as DisplayImage

sealed class Output<T>(defaultValue: T) {

    protected var value by mutableStateOf(defaultValue)

    fun provide(value: T) {
        this.value = value
    }

    @Composable
    abstract fun render()

    class Image : Output<Painter?>(null) {
        @Composable
        override fun render() {
            DisplayImage(
                painter = value ?: return,
                contentDescription = null
            )
        }
    }
}