package io.github.seggan.glagolitsa.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import com.composeunstyled.Text
import com.composeunstyled.theme.Theme
import io.github.seggan.glagolitsa.node.Node
import io.github.seggan.glagolitsa.node.Port
import io.github.seggan.glagolitsa.node.impl.LoadImageNode
import io.github.seggan.glagolitsa.node.impl.SaveFitsNode

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun App() = LightTheme {
    val nodes = remember { mutableStateMapOf<Node, Offset>() }
    var scale by remember { mutableStateOf(1f) }

    var currentlyConnectingPort by remember { mutableStateOf<Pair<Port<*>, Offset>?>(null) }

    val contextMenuState = remember { ContextMenuState() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme[colors][background])
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    for ((node, offset) in nodes) {
                        nodes[node] = offset + dragAmount
                    }
                }
            }
            .onPointerEvent(PointerEventType.Scroll) { event ->
                for (change in event.changes) {
                    val scrollDelta = change.scrollDelta.y
                    val scaleFactor = if (scrollDelta > 0) 1.1f else 0.9f
                    val oldScale = scale
                    scale *= scaleFactor
                    val pointerPosition = change.position

                    fun offset(offset: Offset): Offset {
                        val pointerCenteredOffset = offset - pointerPosition
                        val newOffset = pointerPosition + pointerCenteredOffset * (scale / oldScale)
                        return newOffset
                    }

                    for ((node, offset) in nodes) {
                        nodes[node] = offset(offset)
                    }

                    change.consume()
                }
            }
            .contextMenu(contextMenuState)
    ) {

        ContextMenu(contextMenuState) {
            @Composable
            fun NodeButton(
                name: String,
                constructor: () -> Node
            ) {
                ContextMenuItem(
                    text = { Text(name) },
                    onClick = {
                        val node = constructor()
                        nodes[node] = (contextMenuState.status as ContextMenuState.Status.Open).position
                    }
                )
            }
            ContextSubmenu(text = { Text("Add Node") }) {
                ContextSubmenu(text = { Text("File") }) {
                    NodeButton("Load Image", ::LoadImageNode)
                    NodeButton("Save Image as FITS", ::SaveFitsNode)
                }
            }
        }

        // Render nodes
        for ((node, offset) in nodes) {
            NodeView(
                node = node,
                offset = offset,
                scale = scale,
                onRemove = {
                    for (port in node.inPorts) {
                        val connected = port.connectedTo
                        if (connected != null) {
                            connected.connectedTo.remove(port)
                            port.connectedTo = null
                        }
                    }
                    for (port in node.outPorts) {
                        for (connected in port.connectedTo) {
                            connected.connectedTo = null
                        }
                        port.connectedTo.clear()
                    }
                    nodes.remove(node)
                },
                onDrag = { dragAmount ->
                    nodes[node] = nodes[node]!! + dragAmount * scale
                },
                onPortDrag = onPortDrag@{ port, pointerRel ->
                    if (pointerRel != null) {
                        val connecting = currentlyConnectingPort
                        if (connecting != null) {
                            val input = connecting.first
                            if (input is Port.Input) {
                                val connected = input.connectedTo
                                if (connected != null) {
                                    connected.connectedTo.remove(input)
                                    input.connectedTo = null
                                    currentlyConnectingPort =
                                        connected to pointerRel * scale + port.worldPosition - connected.worldPosition
                                    return@onPortDrag
                                }
                            }
                            currentlyConnectingPort =
                                input to pointerRel * scale + port.worldPosition - input.worldPosition
                        } else {
                            currentlyConnectingPort = port to pointerRel * scale
                        }
                    } else {
                        val connecting = currentlyConnectingPort
                        if (connecting != null) {
                            val (port, pointerRel) = connecting
                            val pointerWorld = pointerRel + port.worldPosition
                            for (node in nodes.keys) {
                                for (destPort in if (port is Port.Input) node.outPorts else node.inPorts) {
                                    val dist =
                                        (destPort.worldPosition + Port.CENTER_OFFSET * scale - pointerWorld).getDistanceSquared()
                                    if (dist < Port.RADIUS * Port.RADIUS * scale * scale && destPort.type == port.type) {
                                        port.connect(destPort)
                                    }
                                }
                            }

                            currentlyConnectingPort = null
                        }
                    }
                }
            )
        }

        // Render connections
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            fun drawBezier(origin: Offset, rel: Offset, color: Color) {
                val path = Path()
                path.moveTo(origin.x, origin.y)
                path.relativeCubicTo(
                    rel.x * 0.3f, 0f,
                    rel.x - rel.x * 0.3f, rel.y,
                    rel.x, rel.y
                )
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = 4f * scale)
                )
            }

            val from = currentlyConnectingPort
            if (from != null) {
                val (port, pointerRel) = from
                drawBezier(
                    origin = port.worldPosition + Port.CENTER_OFFSET * scale,
                    rel = pointerRel - Port.CENTER_OFFSET * scale,
                    color = port.type.color
                )
            }

            for (node in nodes.keys) {
                for (output in node.outPorts) {
                    val outputPos = output.worldPosition + Port.CENTER_OFFSET * scale
                    for (input in output.connectedTo) {
                        val inputPos = input.worldPosition + Port.CENTER_OFFSET * scale
                        drawBezier(
                            origin = outputPos,
                            rel = inputPos - outputPos,
                            color = output.type.color
                        )
                    }
                }
            }
        }
    }
}