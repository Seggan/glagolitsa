package io.github.seggan.glagolitsa.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.composeunstyled.Icon
import com.composeunstyled.UnstyledButton
import com.composeunstyled.theme.Theme
import glagolitsa.generated.resources.Res
import glagolitsa.generated.resources.chevron_right
import org.jetbrains.compose.resources.painterResource

@Composable
fun ContextMenu(
    state: ContextMenuState,
    modifier: Modifier = Modifier,
    content: @Composable ContextMenuScope.() -> Unit
) {
    val status = state.status
    if (status is ContextMenuState.Status.Open) {
        Popup(
            alignment = Alignment.TopStart,
            offset = IntOffset(status.position.x.toInt(), status.position.y.toInt()),
            onDismissRequest = {
                state.status = ContextMenuState.Status.Closed
            },
        ) {
            Column(
                modifier = modifier
                    .padding(2.dp)
                    .border(Dp.Hairline, color = Theme[colors][outline], shape = RoundedCornerShape(1.dp))
                    .width(IntrinsicSize.Max)
                    .height(IntrinsicSize.Min),
            ) {
                val scope = object : ContextMenuScope, ColumnScope by this {
                    override val rootState = state
                }
                scope.content()
            }
        }
    }
}

fun Modifier.contextMenu(state: ContextMenuState): Modifier = pointerInput(Unit) {
    awaitEachGesture {
        var event: PointerEvent
        do {
            event = awaitPointerEvent()
        } while (!event.changes.all { it.changedToDown() })
        if (event.buttons.isSecondaryPressed) {
            event.changes.forEach { it.consume() }
            state.status = ContextMenuState.Status.Open(event.changes.first().position)
        }
    }
}

class ContextMenuState {
    sealed interface Status {
        data class Open(val position: Offset) : Status
        object Closed : Status
    }

    var status: Status by mutableStateOf(Status.Closed)

    var childOpen: Boolean by mutableStateOf(false)
}

interface ContextMenuScope : ColumnScope {
    val rootState: ContextMenuState
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContextMenuScope.ContextMenuItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: @Composable RowScope.() -> Unit,
) {
    UnstyledButton(
        onClick = {
            onClick()
            rootState.status = ContextMenuState.Status.Closed
        },
        modifier = modifier
            .background(color = Theme[colors][background], shape = RoundedCornerShape(1.dp))
            .padding(3.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 3.dp, horizontal = 5.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            text()
        }
    }
}

@Composable
fun ContextMenuScope.ContextSubmenu(
    text: @Composable () -> Unit,
    content: @Composable ContextMenuScope.() -> Unit
) {
    var openPos by remember { mutableStateOf(Offset.Zero) }

    val buttonInteraction = remember { MutableInteractionSource() }
    val isHovered by buttonInteraction.collectIsHoveredAsState()
    val menuInteraction = remember { MutableInteractionSource() }
    val isMenuHovered by menuInteraction.collectIsHoveredAsState()

    val submenuState = remember { ContextMenuState() }

    ContextMenuItem(
        onClick = {},
        modifier = Modifier
            .hoverable(buttonInteraction)
            .onGloballyPositioned {
                openPos = it.positionInParent()
                openPos = openPos.copy(x = openPos.x + it.size.width, y = openPos.y - 2)
            }
    ) {
        text()
        Icon(painterResource(Res.drawable.chevron_right), contentDescription = null)
    }

    if (isHovered || isMenuHovered || submenuState.childOpen) {
        submenuState.status = ContextMenuState.Status.Open(openPos)
        rootState.childOpen = true
    } else {
        submenuState.status = ContextMenuState.Status.Closed
        rootState.childOpen = false
    }

    if (rootState.status is ContextMenuState.Status.Closed) {
        submenuState.status = ContextMenuState.Status.Closed
    }

    ContextMenu(
        state = submenuState,
        modifier = Modifier.hoverable(menuInteraction)
    ) {
        content()
    }
}