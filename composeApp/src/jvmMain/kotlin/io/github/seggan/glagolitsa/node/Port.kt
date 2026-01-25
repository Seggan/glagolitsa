package io.github.seggan.glagolitsa.node

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

sealed class Port {
    enum class Type(val color: Color) {
        IMAGE(Color(0xFFFFDD00)),
    }

    abstract val name: String
    abstract val type: Type

    var worldPosition by mutableStateOf(Offset.Zero)

    data class Input(
        override val name: String,
        override val type: Type,
        var connectedTo: Output? = null
    ) : Port()

    data class Output(
        override val name: String,
        override val type: Type,
        val connectedTo: MutableList<Input> = mutableListOf()
    ) : Port()

    companion object {
        const val RADIUS = 7f
        val CENTER_OFFSET = Offset(RADIUS, RADIUS)
    }
}
