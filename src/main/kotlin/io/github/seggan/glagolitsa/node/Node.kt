package io.github.seggan.glagolitsa.node

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.seggan.glagolitsa.node.impl.LoadImageNode
import io.github.seggan.glagolitsa.node.impl.SaveFitsNode
import kotlin.properties.ReadOnlyProperty

abstract class Node<N : Node<N>> {

    abstract val spec: Spec<N>

    var state by mutableStateOf<State>(State.Idle)
        protected set

    val inPorts = mutableListOf<Port.Input<*>>()

    val outPorts = mutableListOf<Port.Output<*>>()

    val parameters = mutableListOf<Parameter<*>>()

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

    protected fun updateProgress(progress: Float?) {
        state = State.Running(progress)
    }

    protected operator fun <T> Port.Input<T>.provideDelegate(thisRef: Any?, prop: kotlin.reflect.KProperty<*>): ReadOnlyProperty<Any?, Port.Input<T>> {
        inPorts.add(this)
        return ReadOnlyProperty { _, _ -> this }
    }

    protected operator fun <T> Port.Output<T>.provideDelegate(thisRef: Any?, prop: kotlin.reflect.KProperty<*>): ReadOnlyProperty<Any?, Port.Output<T>> {
        outPorts.add(this)
        return ReadOnlyProperty { _, _ -> this }
    }

    protected operator fun <T> Parameter<T>.provideDelegate(thisRef: Any?, prop: kotlin.reflect.KProperty<*>): ReadOnlyProperty<Any?, T> {
        parameters.add(this)
        return ReadOnlyProperty { _, _ -> value }
    }

    companion object {
        val TYPES = mapOf(
            LoadImageNode.id to LoadImageNode,
            SaveFitsNode.id to SaveFitsNode,
        )
    }

    sealed interface State {
        data object Idle : State
        data class Running(val progress: Float?) : State
        data object Success : State
        data class Error(val message: String) : State
    }

    abstract class Spec<N : Node<N>> protected constructor() {
        abstract val id: String
        abstract val name: String

        abstract fun construct(): N
    }
}

class NodeException(message: String) : Exception(message)