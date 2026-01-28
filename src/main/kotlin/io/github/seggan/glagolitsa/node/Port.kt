package io.github.seggan.glagolitsa.node

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import java.nio.file.Path
import kotlin.io.path.deleteIfExists

sealed class Port<T>(val node: Node<*>) {

    sealed class Type<T>(val color: Color) {
        data object Image : Type<Path>(Color(0xFFAA3838))
    }

    abstract val name: String
    abstract val type: Type<T>

    var worldPosition by mutableStateOf(Offset.Zero)

    abstract fun connect(other: Port<*>)

    class Input<T>(
        node: Node<*>,
        override val name: String,
        override val type: Type<T>,
    ) : Port<T>(node) {

        var connectedTo: Output<T>? = null

        override fun connect(other: Port<*>) {
            if (other is Output<*> && other.type == type) {
                @Suppress("UNCHECKED_CAST")
                connectedTo = other as Output<T>
                other.connectedTo.add(this)
            } else {
                throw IllegalArgumentException("Cannot connect port of type ${other.type} to input of type $type")
            }
        }

        suspend fun getValue(): T {
            val connected = connectedTo ?: throw NodeException("Port $name is not connected")
            if (connected.value == null) {
                connected.node.execute()
            }
            return connected.value ?: throw NodeException("Port $name has not received a value")
        }
    }

    class Output<T>(
        node: Node<*>,
        override val name: String,
        override val type: Type<T>,
    ) : Port<T>(node) {

        val connectedTo: MutableList<Input<T>> = mutableListOf()

        var value: T? by mutableStateOf(null)
            private set

        fun flush() {
            val valueValue = value
            if (valueValue is Path) {
                valueValue.deleteIfExists()
            }
            value = null
        }

        fun provide(newValue: T) {
            value = newValue
        }

        override fun connect(other: Port<*>) {
            if (other is Input<*> && other.type == type) {
                @Suppress("UNCHECKED_CAST")
                connectedTo.add(other as Input<T>)
                other.connectedTo = this
            } else {
                throw IllegalArgumentException("Cannot connect port of type ${other.type} to output of type $type")
            }
        }
    }

    companion object {
        const val RADIUS = 7f
        val CENTER_OFFSET = Offset(RADIUS, RADIUS)
    }
}
