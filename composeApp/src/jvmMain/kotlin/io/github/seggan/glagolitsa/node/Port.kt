package io.github.seggan.glagolitsa.node

import androidx.compose.ui.graphics.Color
import java.util.UUID

sealed interface Port {
    enum class Type(val color: Color) {
        IMAGE(Color(0xFFFFDD00)),
    }

    val name: String
    val type: Type

    data class Input(
        override val name: String,
        override val type: Type,
        var connectedTo: Output? = null
    ) : Port

    data class Output(
        override val name: String,
        override val type: Type,
        val connectedTo: MutableList<Input> = mutableListOf()
    ) : Port
}
