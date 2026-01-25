package io.github.seggan.glagolitsa.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.seggan.glagolitsa.node.Node
import io.github.seggan.glagolitsa.node.Port

@Composable
fun NodeView(
    node: Node,
    offset: Offset,
    scale: Float,
    onDrag: (Offset) -> Unit = {},
    onPortDrag: (port: Port, portOffset: Offset, centerOffset: Offset, pointerRel: Offset?) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier,
) {
    var size by remember { mutableStateOf(Offset.Zero) }
    Box(
        modifier = modifier
            .onGloballyPositioned {
                size = Offset(it.size.width.toFloat(), it.size.height.toFloat())
            }
            .graphicsLayer {
                translationX = offset.x - size.x / 2
                translationY = offset.y - size.y / 2
                scaleX = scale
                scaleY = scale
            }
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
            for (input in node.inputs) {
                Row(verticalAlignment = Alignment.CenterVertically) { input.generate() }
            }
            Box {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (input in node.inPorts) {
                        Row(
                            modifier = Modifier
                                .offset(x = (-7).dp - contentPadding),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(14.dp)
                                    .background(input.type.color)
                            )
                            Text(input.name, modifier = Modifier.padding(2.dp))
                        }
                    }
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (output in node.outPorts) {
                        Row(
                            modifier = Modifier
                                .offset(x = 7.dp + contentPadding),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(output.name, modifier = Modifier.padding(2.dp))

                            var position by remember { mutableStateOf(Offset.Zero) }
                            var centerOffset by remember { mutableStateOf(Offset.Zero) }
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(14.dp)
                                    .background(output.type.color)
                                    .onGloballyPositioned {
                                        position = it.positionInRoot()
                                        centerOffset = Offset(
                                            it.size.width / 2f,
                                            it.size.height / 2f
                                        )
                                    }
                                    .pointerInput(Unit) {
                                        detectDragGestures(
                                            onDrag = { change, _ ->
                                                onPortDrag(output, position, centerOffset, change.position)
                                            },
                                            onDragEnd = {
                                                onPortDrag(output, position, centerOffset, null)
                                            },
                                            onDragCancel = {
                                                onPortDrag(output, position, centerOffset, null)
                                            }
                                        )
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}