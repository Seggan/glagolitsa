package io.github.seggan.glagolitsa.node

import androidx.compose.ui.geometry.Offset
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

fun saveToJson(nodes: Map<Node<*>, Offset>): JsonElement {
    val nodeList = nodes.keys.toList()
    val savedNodes = mutableListOf<SavedNode>()
    for (node in nodeList) {
        val offset = nodes[node]!!
        val parameters = node.parameters.map { it.saveValue() }
        val connections = mutableMapOf<Int, Set<Pair<Int, Int>>>()
        for ((outIndex, outPort) in node.outPorts.withIndex()) {
            val connected = mutableSetOf<Pair<Int, Int>>()
            for (connectedPort in outPort.connectedTo) {
                val connectedNodeIndex = nodeList.indexOf(connectedPort.node)
                val inIndex = connectedPort.node.inPorts.indexOf(connectedPort)
                if (connectedNodeIndex != -1 && inIndex != -1) {
                    connected.add(Pair(connectedNodeIndex, inIndex))
                }
            }
            connections[outIndex] = connected
        }
        savedNodes.add(
            SavedNode(
                id = node.spec.id,
                x = offset.x,
                y = offset.y,
                parameters = parameters,
                connections = connections
            )
        )
    }
    return Json.encodeToJsonElement(savedNodes)
}

fun loadFromJson(
    jsonElement: JsonElement,
    nodeOut: MutableMap<Node<*>, Offset>
) {
    val savedNodes = Json.decodeFromJsonElement<List<SavedNode>>(jsonElement)
    val nodeList = mutableListOf<Node<*>>()
    for (savedNode in savedNodes) {
        val spec = Node.TYPES[savedNode.id]
            ?: throw IllegalArgumentException("Unknown node type: ${savedNode.id}")
        val node = spec.construct()
        for ((param, savedValue) in node.parameters.zip(savedNode.parameters)) {
            param.loadValue(savedValue)
        }
        nodeList.add(node)
        nodeOut[node] = Offset(savedNode.x, savedNode.y)
    }
    for ((savedNode, node) in savedNodes.zip(nodeList)) {
        for ((outIndex, connections) in savedNode.connections) {
            val outPort = node.outPorts[outIndex]
            for ((connectedNodeIndex, inIndex) in connections) {
                val connectedNode = nodeList[connectedNodeIndex]
                val inPort = connectedNode.inPorts[inIndex]
                outPort.connect(inPort)
            }
        }
    }
}

@Serializable
private data class SavedNode(
    val id: String,
    val x: Float,
    val y: Float,
    val parameters: List<JsonElement>,
    val connections: Map<Int, Set<Pair<Int, Int>>>
)