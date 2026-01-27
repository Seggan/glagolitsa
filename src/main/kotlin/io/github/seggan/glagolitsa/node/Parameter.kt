package io.github.seggan.glagolitsa.node

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.composeunstyled.Text
import com.composeunstyled.TextField
import com.composeunstyled.UnstyledButton
import com.composeunstyled.theme.Theme
import io.github.seggan.glagolitsa.ui.colors
import io.github.seggan.glagolitsa.ui.outline
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.toKotlinxIoPath
import io.github.vinceglb.filekit.utils.toFile
import kotlinx.coroutines.launch
import java.nio.file.Path
import kotlin.io.path.name

sealed class Parameter<T>(initialValue: T) {

    var value: T by mutableStateOf(initialValue)

    @Composable
    abstract fun generate()

    class FilePicker(private val type: FileKitType) : Parameter<Path?>(null) {
        @Composable
        override fun generate() {
            val scope = rememberCoroutineScope()
            Text("File: ")
            UnstyledButton(
                onClick = {
                    scope.launch {
                        val file = FileKit.openFilePicker(
                            type = type,
                            directory = PlatformFile(System.getProperty("user.dir"))
                        )
                        value = file?.toKotlinxIoPath()?.toFile()?.toPath()
                    }
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .border(1.dp, color = Theme[colors][outline], shape = RoundedCornerShape(5.dp))
            ) {
                Text(text = value?.name ?: "Select file", modifier = Modifier.padding(vertical = 2.dp, horizontal = 5.dp))
            }
        }
    }

    class SaveFilePicker(private val suggestedName: String, private val suggestedExtension: String) : Parameter<Path?>(null) {
        @Composable
        override fun generate() {
            val scope = rememberCoroutineScope()
            Text("File: ")
            UnstyledButton(
                onClick = {
                    scope.launch {
                        val file = FileKit.openFileSaver(
                            suggestedName = suggestedName,
                            extension = suggestedExtension,
                            directory = PlatformFile(System.getProperty("user.dir"))
                        )
                        value = file?.toKotlinxIoPath()?.toFile()?.toPath()
                    }
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .border(1.dp, color = Theme[colors][outline], shape = RoundedCornerShape(5.dp))
            ) {
                Text(text = value?.name ?: "Select file", modifier = Modifier.padding(vertical = 2.dp, horizontal = 5.dp))
            }
        }
    }

    class Integer(private val label: String, initialValue: Int, private val range: IntRange? = null) : Parameter<Int>(initialValue) {
        @Composable
        override fun generate() {
            Text("$label: ")
            TextField(
                state = rememberTextFieldState(value.toString())
            ) {

            }
        }
    }
}