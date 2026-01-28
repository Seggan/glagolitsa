package io.github.seggan.glagolitsa.node

import androidx.compose.ui.geometry.Offset
import io.github.seggan.glagolitsa.node.impl.AutoStretchNode
import io.github.seggan.glagolitsa.node.impl.LoadImageNode
import io.github.seggan.glagolitsa.node.impl.PreviewNode
import io.github.seggan.glagolitsa.node.impl.SaveFitsNode
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

fun saveToJson(scale: Float, nodes: Map<Node<*>, Offset>): JsonElement {
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
    val nodes = Json.encodeToJsonElement(savedNodes)
    return buildJsonObject {
        put("scale", scale)
        put("nodes", nodes)
    }
}

private val types = mapOf(
    AutoStretchNode.id to AutoStretchNode,
    LoadImageNode.id to LoadImageNode,
    PreviewNode.id to PreviewNode,
    SaveFitsNode.id to SaveFitsNode,
)

fun loadFromJson(
    jsonElement: JsonElement
): Pair<Float, Map<Node<*>, Offset>> {
    val obj = jsonElement.jsonObject
    val scale = obj["scale"]!!.jsonPrimitive.float
    val savedNodes = Json.decodeFromJsonElement<List<SavedNode>>(obj["nodes"]!!)
    val nodeList = mutableListOf<Node<*>>()
    val nodeOut = mutableMapOf<Node<*>, Offset>()
    for (savedNode in savedNodes) {
        val spec = types[savedNode.id]
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
    return scale to nodeOut
}

@Serializable
private data class SavedNode(
    val id: String,
    val x: Float,
    val y: Float,
    val parameters: List<JsonElement>,
    val connections: Map<Int, Set<Pair<Int, Int>>>
)