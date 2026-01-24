package io.github.seggan.glagolitsa.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import io.github.seggan.glagolitsa.node.Node
import io.github.seggan.glagolitsa.node.impl.LoadImage
import kotlin.collections.getValue

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App() = MaterialTheme {
    val nodes = remember { mutableStateMapOf<Node, Offset>(LoadImage() to Offset.Zero) }
    var scale by remember { mutableStateOf(1f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
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
                    for ((node, offset) in nodes) {
                        val pointerCenteredOffset = pointerPosition - offset
                        val newOffset = pointerPosition - (pointerCenteredOffset * (scale / oldScale))
                        nodes[node] = newOffset
                    }
                    change.consume()
                }
            }
    ) {
        for ((node, offset) in nodes) {
            NodeView(
                node = node,
                offset = offset,
                scale = scale,
                onDrag = { dragAmount ->
                    nodes[node] = nodes[node]!! + dragAmount * scale
                },
            )
        }
    }
}