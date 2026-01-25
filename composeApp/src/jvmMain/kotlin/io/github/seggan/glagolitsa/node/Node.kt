package io.github.seggan.glagolitsa.node

interface Node {
    val name: String

    val inPorts: List<Port.Input>
        get() = emptyList()

    val outPorts: List<Port.Output>
        get() = emptyList()

    val parameters: List<Parameter<*>>
        get() = emptyList()
}