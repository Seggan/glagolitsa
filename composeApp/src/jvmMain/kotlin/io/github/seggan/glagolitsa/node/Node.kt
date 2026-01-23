package io.github.seggan.glagolitsa.node

interface Node {
    val name: String

    val inPorts: List<Port>
    val outPorts: List<Port>

    val inputs: List<Input<*>>
}