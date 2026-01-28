package io.github.seggan.glagolitsa.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composables.core.HorizontalSeparator
import com.composeunstyled.Icon
import com.composeunstyled.ProgressIndicator
import com.composeunstyled.Text
import com.composeunstyled.theme.Theme
import glagolitsa.generated.resources.Res
import glagolitsa.generated.resources.play_arrow
import glagolitsa.generated.resources.progress_activity
import io.github.seggan.glagolitsa.node.Node
import io.github.seggan.glagolitsa.node.Port
import io.github.seggan.glagolitsa.node.impl.AutoStretchNode
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Preview
@Composable
private fun NodeViewPreview() = LightTheme {
    NodeView(
        node = AutoStretchNode(),
        offset = Offset(0f, 0f),
        scale = 1f
    )
}

@Composable
fun NodeView(
    node: Node<*>,
    offset: Offset,
    scale: Float,
    onDrag: (Node<*>, Offset) -> Unit = { _, _ -> },
    onPortDrag: (Port<*>, Offset?) -> Unit = { _, _ -> },
    onRemove: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    var size by remember { mutableStateOf(Offset.Zero) }
    Box(
        modifier = modifier
            .onGloballyPositioned {
                size = Offset(it.size.width.toFloat(), it.size.height.toFloat())
            }
            .offset(
                x = (offset.x - size.x / 2).dp,
                y = offset.y.dp
            )
            .scale(scale)
            .background(Theme[colors][background], RoundedCornerShape(10.dp))
            .width(IntrinsicSize.Max)
            .height(IntrinsicSize.Min)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(node, dragAmount)
                }
            }
    ) {
        val nodeState = node.state
        val contextMenuState = remember { ContextMenuState() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .contextMenu(contextMenuState)
        ) {
            ContextMenu(contextMenuState) {
                ContextMenuItem(
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
                .border(
                    width = 1.dp,
                    color = when (node.state) {
                        is Node.State.Idle -> Color.Gray
                        is Node.State.Running -> Color.Blue
                        is Node.State.Success -> Color.Green
                        is Node.State.Error -> Color.Red
                    },
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(16.dp)
        )
        Column(
            modifier = Modifier.padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (nodeState is Node.State.Running) {
                    if (nodeState.progress != null) {
                        ProgressIndicator(
                            progress = nodeState.progress,
                            modifier = Modifier.size(24.dp),
                            shape = CircleShape,
                            backgroundColor = Theme[colors][background],
                            contentColor = Color.Blue
                        ) {
                            TODO()
                        }
                    } else {
                        ProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            shape = CircleShape,
                            backgroundColor = Theme[colors][background],
                            contentColor = Color.Blue
                        ) {
                            val transition = rememberInfiniteTransition()
                            val rotation by transition.animateFloat(
                                initialValue = 0f,
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Restart
                                )
                            )
                            Icon(
                                painter = painterResource(Res.drawable.progress_activity),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                                    .rotate(rotation)
                            )
                        }
                    }
                } else {
                    Icon(
                        painter = painterResource(Res.drawable.play_arrow),
                        contentDescription = "Execute",
                        modifier = Modifier
                            .clickable {
                                scope.launch {
                                    node.execute()
                                }
                            }
                            .size(24.dp)
                    )
                }
                Text(node.spec.name, textAlign = TextAlign.Center, modifier = Modifier.height(24.dp))
            }
            HorizontalSeparator(modifier = Modifier.padding(vertical = 5.dp), color = Theme[colors][onBackground])
            for (parameter in node.parameters) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(5.dp)
                ) { parameter.generate() }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
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

            if (nodeState is Node.State.Error) {
                HorizontalSeparator(
                    modifier = Modifier
                        .padding(vertical = 5.dp),
                    color = Color.Red
                )
                Text(
                    text = nodeState.message,
                    color = Color.Red
                )
            }
        }
    }
}

@Composable
private fun Port(
    port: Port<*>,
    offset: Dp,
    onDrag: (Port<*>, Offset?) -> Unit
) {
    Row(
        modifier = Modifier
            .offset(x = offset),
        horizontalArrangement = if (port is Port.Input<*>) Arrangement.Start else Arrangement.End,
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