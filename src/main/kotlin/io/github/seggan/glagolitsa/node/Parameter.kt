package io.github.seggan.glagolitsa.node

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.composeunstyled.Text
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
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.name

sealed class Parameter<T>(initialValue: T) {

    var value: T by mutableStateOf(initialValue)

    @Composable
    abstract fun generate()

    abstract fun saveValue(): JsonElement
    abstract fun loadValue(element: JsonElement)

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

        override fun saveValue(): JsonElement {
            return JsonPrimitive(value?.toString())
        }

        override fun loadValue(element: JsonElement) {
            val pathString = element.jsonPrimitive.contentOrNull
            value = pathString?.let(::Path)
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

        override fun saveValue(): JsonElement {
            return JsonPrimitive(value?.toString())
        }

        override fun loadValue(element: JsonElement) {
            val pathString = element.jsonPrimitive.contentOrNull
            value = pathString?.let(::Path)
        }
    }
}