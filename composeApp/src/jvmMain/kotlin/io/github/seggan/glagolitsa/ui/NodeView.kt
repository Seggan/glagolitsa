package io.github.seggan.glagolitsa.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.seggan.glagolitsa.node.Node
import io.github.seggan.glagolitsa.node.Port

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NodeView(
    node: Node,
    offset: Offset,
    scale: Float,
    onDrag: (Offset) -> Unit = {},
    onPortDrag: (Port, Offset?) -> Unit = { _, _ -> },
    onRemove: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var size by remember { mutableStateOf(Offset.Zero) }
    Box(
        modifier = modifier
            .onGloballyPositioned {
                size = Offset(it.size.width.toFloat(), it.size.height.toFloat())
            }
            .offset(
                x = (offset.x - size.x / 2).dp,
                y = (offset.y - size.y / 2).dp
            )
            .scale(scale)
            .background(MaterialTheme.colors.background, RoundedCornerShape(10.dp))
            .width(IntrinsicSize.Max)
            .height(IntrinsicSize.Min)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount)
                }
            }
    ) {
        val state = remember { DropdownMenuState() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .contextMenu(state)
        ) {
            DropdownMenu(state) {
                DropdownMenuItem(
                    text = { Text("Remove") },
                    onClick = onRemove
                )
            }
        }
        val contentPadding = 16.dp
        // Border drawn separately to be below the inner content
        Box(
            modifier = modifier
                .matchParentSize()
                .border(1.dp, Color.DarkGray, RoundedCornerShape(10.dp))
                .padding(16.dp)
        )
        Column(
            modifier = Modifier.padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(node.name, textAlign = TextAlign.Center)
            HorizontalDivider(modifier = Modifier.padding(vertical = 5.dp))
            for (parameter in node.parameters) {
                Row(verticalAlignment = Alignment.CenterVertically) { parameter.generate() }
            }
            Box {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (input in node.inPorts) {
                        Port(
                            port = input,
                            offset = (-Port.RADIUS).dp - contentPadding,
                            onDrag = onPortDrag
                        )
                    }
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (output in node.outPorts) {
                        Port(
                            port = output,
                            offset = Port.RADIUS.dp + contentPadding,
                            onDrag = onPortDrag
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Port(
    port: Port,
    offset: Dp,
    onDrag: (Port, Offset?) -> Unit
) {
    Row(
        modifier = Modifier
            .offset(x = offset),
        horizontalArrangement = if (port is Port.Input) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (port is Port.Output) {
            Text(port.name, modifier = Modifier.padding(2.dp))
        }

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size((Port.RADIUS * 2).dp)
                .background(port.type.color)
                .onGloballyPositioned {
                    port.worldPosition = it.positionInRoot()
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, _ ->
                            onDrag(port, change.position)
                        },
                        onDragEnd = {
                            onDrag(port, null)
                        },
                        onDragCancel = {
                            onDrag(port, null)
                        }
                    )
                }
        )

        if (port is Port.Input) {
            Text(port.name, modifier = Modifier.padding(2.dp))
        }
    }
}