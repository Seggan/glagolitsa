package io.github.seggan.glagolitsa.node

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.seggan.glagolitsa.node.impl.LoadImageNode
import io.github.seggan.glagolitsa.node.impl.SafeFitsNode
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.properties.ReadOnlyProperty

abstract class Node {

    abstract val name: String

    var state by mutableStateOf<State>(State.Idle)
        protected set

    val inPorts = mutableListOf<Port.Input<*>>()

    protected operator fun <T> Port.Input<T>.provideDelegate(thisRef: Any?, prop: kotlin.reflect.KProperty<*>): ReadOnlyProperty<Any?, Port.Input<T>> {
        inPorts.add(this)
        return ReadOnlyProperty { _, _ -> this }
    }

    val outPorts = mutableListOf<Port.Output<*>>()

    protected operator fun <T> Port.Output<T>.provideDelegate(thisRef: Any?, prop: kotlin.reflect.KProperty<*>): ReadOnlyProperty<Any?, Port.Output<T>> {
        outPorts.add(this)
        return ReadOnlyProperty { _, _ -> this }
    }

    val parameters = mutableListOf<Parameter<*>>()

    protected operator fun <T> Parameter<T>.provideDelegate(thisRef: Any?, prop: kotlin.reflect.KProperty<*>): ReadOnlyProperty<Any?, T> {
        parameters.add(this)
        return ReadOnlyProperty { _, _ -> value }
    }

    private fun flush() {
        state = State.Idle
        for (out in outPorts) {
            out.flush()
            for (connected in out.connectedTo) {
                connected.node.flush()
            }
        }
    }

    suspend fun execute() {
        flush()
        state = State.Running(null)
        try {
            executeInternal()
            state = State.Success
        } catch (e: NodeException) {
            state = State.Error(e.message ?: "Unknown error")
        }
    }

    protected abstract suspend fun executeInternal()

    protected fun getRandomFile(ext: String = ""): Path {
        TEMP_DIR.createDirectories()
        val filename = List(16) {
            ('a'..'z') + ('0'..'9')
        }.flatten().shuffled().take(16).joinToString("")
        return TEMP_DIR.resolve(filename + ext)
    }

    protected fun updateProgress(progress: Float?) {
        state = State.Running(progress)
    }

    companion object {
        val TYPES = mapOf(
            "Load Image" to ::LoadImageNode,
            "Save Image" to ::SafeFitsNode
        )

        val TEMP_DIR = Path("/home/seggan/.tmp")
    }

    sealed interface State {
        data object Idle : State
        data class Running(val progress: Float?) : State
        data object Success : State
        data class Error(val message: String) : State
    }
}

class NodeException(message: String) : Exception(message)