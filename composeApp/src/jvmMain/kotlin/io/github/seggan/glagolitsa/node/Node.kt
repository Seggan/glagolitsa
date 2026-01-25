package io.github.seggan.glagolitsa.node

import io.github.seggan.glagolitsa.node.impl.LoadImageNode
import io.github.seggan.glagolitsa.node.impl.SaveImageNode

interface Node {
    val name: String

    val inPorts: List<Port.Input>
        get() = emptyList()

    val outPorts: List<Port.Output>
        get() = emptyList()

    val parameters: List<Parameter<*>>
        get() = emptyList()

    companion object {
        val TYPES = mapOf(
            "Load Image" to ::LoadImageNode,
            "Save Image" to ::SaveImageNode
        )
    }
}