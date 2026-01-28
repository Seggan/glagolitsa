package io.github.seggan.glagolitsa.node

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.composeunstyled.Slider
import com.composeunstyled.Text
import com.composeunstyled.UnstyledButton
import com.composeunstyled.rememberSliderState
import com.composeunstyled.theme.Theme
import io.github.seggan.glagolitsa.ui.*
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.toKotlinxIoPath
import io.github.vinceglb.filekit.utils.toFile
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.name

sealed class Parameter<T>(initialValue: T) {

    var value: T by mutableStateOf(initialValue)

    @Composable
    abstract fun render()

    abstract fun saveValue(): JsonElement
    abstract fun loadValue(element: JsonElement)

    class FilePicker(private val type: FileKitType) : Parameter<Path?>(null) {

        @Composable
        override fun render() {
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
                    .background(color = Theme[colors][background])
                    .border(2.dp, color = Theme[colors][outline], shape = RoundedCornerShape(5.dp))
            ) {
                Text(
                    text = value?.name ?: "Select file",
                    modifier = Modifier.padding(vertical = 3.dp, horizontal = 5.dp),
                    color = Theme[colors][onBackground]
                )
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

    class SaveFilePicker(private val suggestedName: String, private val suggestedExtension: String) :
        Parameter<Path?>(null) {

        @Composable
        override fun render() {
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
                    .border(2.dp, color = Theme[colors][outline], shape = RoundedCornerShape(5.dp))
            ) {
                Text(
                    text = value?.name ?: "Select file",
                    modifier = Modifier.padding(vertical = 3.dp, horizontal = 5.dp)
                )
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

    class FloatSlider(private val label: String, private val initialValue: Float, private val range: ClosedFloatingPointRange<Float>) :
        Parameter<Float>(initialValue) {

        @Composable
        override fun render() {
            val sliderState = rememberSliderState(initialValue = initialValue, valueRange = range)
            value = sliderState.value

            Text("$label: ${"%.2f".format(value)}", modifier = Modifier.padding(end = 3.dp))
            Slider(
                state = sliderState,
                modifier = Modifier
                    .width(200.dp)
                    .padding(horizontal = 5.dp),
                track = {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Theme[colors][primary], RoundedCornerShape(10.dp))
                    )
                },
                thumb = {
                    Box(
                        Modifier
                            .size(20.dp)
                            .background(Theme[colors][onPrimary], CircleShape)
                    )
                }
            )

        }

        override fun saveValue(): JsonElement {
            return JsonPrimitive(value)
        }

        override fun loadValue(element: JsonElement) {
            value = element.jsonPrimitive.float
        }
    }
}