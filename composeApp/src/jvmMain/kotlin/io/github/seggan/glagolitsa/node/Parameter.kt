package io.github.seggan.glagolitsa.node

import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
            OutlinedButton(
                onClick = {
                    scope.launch {
                        val file = FileKit.openFilePicker(
                            type = type,
                            directory = PlatformFile(System.getProperty("user.dir"))
                        )
                        value = file?.toKotlinxIoPath()?.toFile()?.toPath()
                    }
                }
            ) {
                Text(value?.name ?: "Select file")
            }
        }
    }

    class SaveFilePicker(private val suggestedName: String, private val suggestedExtension: String) : Parameter<Path?>(null) {
        @Composable
        override fun generate() {
            val scope = rememberCoroutineScope()
            Text("File: ")
            OutlinedButton(
                onClick = {
                    scope.launch {
                        val file = FileKit.openFileSaver(
                            suggestedName = suggestedName,
                            extension = suggestedExtension,
                            directory = PlatformFile(System.getProperty("user.dir"))
                        )
                        value = file?.toKotlinxIoPath()?.toFile()?.toPath()
                    }
                }
            ) {
                Text(value?.name ?: "Select file")
            }
        }
    }
}