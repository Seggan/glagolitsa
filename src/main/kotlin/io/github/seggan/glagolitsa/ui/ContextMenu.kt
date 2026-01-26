//package io.github.seggan.glagolitsa.ui
//
//import androidx.compose.foundation.gestures.awaitEachGesture
//import androidx.compose.foundation.hoverable
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.interaction.collectIsHoveredAsState
//import androidx.compose.foundation.layout.RowScope
//import androidx.compose.material.DropdownMenu
//import androidx.compose.material.DropdownMenuItem
//import androidx.compose.material.DropdownMenuState
//import androidx.compose.material.Icon
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.input.pointer.PointerEvent
//import androidx.compose.ui.input.pointer.changedToDown
//import androidx.compose.ui.input.pointer.isSecondaryPressed
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.layout.onGloballyPositioned
//import androidx.compose.ui.layout.positionInParent
//import androidx.compose.ui.util.fastAll
//import glagolitsa.composeapp.generated.resources.Res
//import glagolitsa.composeapp.generated.resources.chevron_right
//import org.jetbrains.compose.resources.painterResource
//
//@Composable
//fun ContextMenu(
//    state: DropdownMenuState,
//    content: @Composable ContextMenuScope.() -> Unit
//) {
//    val scope = object : ContextMenuScope {
//        override val state = state
//    }
//    DropdownMenu(state) {
//        scope.content()
//    }
//}
//
//fun Modifier.contextMenu(state: DropdownMenuState): Modifier = pointerInput(Unit) {
//    awaitEachGesture {
//        var event: PointerEvent
//        do {
//            event = awaitPointerEvent()
//        } while (!event.changes.fastAll { it.changedToDown() })
//        if (event.buttons.isSecondaryPressed) {
//            event.changes.forEach { it.consume() }
//            state.status = DropdownMenuState.Status.Open(event.changes.first().position)
//        }
//    }
//}
//
//interface ContextMenuScope {
//    val state: DropdownMenuState
//}
//
//@Composable
//fun ContextMenuScope.ContextMenuItem(
//    text: @Composable RowScope.() -> Unit,
//    onClick: () -> Unit
//) {
//    DropdownMenuItem(
//        onClick = {
//            onClick()
//            state.status = DropdownMenuState.Status.Closed
//        },
//        content = text
//    )
//}
//
//@Composable
//fun ContextMenuScope.ContextSubmenu(
//    text: @Composable () -> Unit,
//    content: @Composable ContextMenuScope.() -> Unit
//) {
//    var openPos by remember { mutableStateOf(Offset.Zero) }
//
//    val buttonInteraction = remember { MutableInteractionSource() }
//    val isHovered by buttonInteraction.collectIsHoveredAsState()
//    val menuInteraction = remember { MutableInteractionSource() }
//    val isMenuHovered by menuInteraction.collectIsHoveredAsState()
//
//    val submenuState = remember { DropdownMenuState() }
//
//    if (isHovered || isMenuHovered) {
//        submenuState.status = DropdownMenuState.Status.Open(openPos)
//    } else {
//        submenuState.status = DropdownMenuState.Status.Closed
//    }
//
//    if (state.status is DropdownMenuState.Status.Closed) {
//        submenuState.status = DropdownMenuState.Status.Closed
//    }
//
//    DropdownMenuItem(
//        onClick = {},
//        modifier = Modifier
//            .hoverable(buttonInteraction)
//            .onGloballyPositioned {
//                openPos = it.positionInParent()
//                openPos = openPos.copy(x = openPos.x + it.size.width)
//            }
//    ) {
//        text()
//        Icon(painterResource(Res.drawable.chevron_right), contentDescription = null)
//    }
//
//    DropdownMenu(
//        state = submenuState,
//        modifier = Modifier.hoverable(menuInteraction)
//    ) {
//        content()
//    }
//}