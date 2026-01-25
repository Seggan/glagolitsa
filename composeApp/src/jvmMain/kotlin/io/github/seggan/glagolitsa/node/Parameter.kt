package io.github.seggan.glagolitsa.node

import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import java.awt.FileDialog
import java.awt.Frame
import java.nio.file.Path

sealed class Parameter<T>(initialValue: T) {

    var value: T by mutableStateOf(initialValue)

    @Composable
    abstract fun generate()

    class File(private vararg val allowedExtensions: String) : Parameter<Path?>(null) {
        @Composable
        override fun generate() {
            val scope = rememberCoroutineScope()
            Text("File: ")
            OutlinedButton(
                onClick = {
                    scope.launch {
                        val files = runInterruptible(Dispatchers.IO) {
                            FileDialog(null as Frame?, "Select file", FileDialog.LOAD).apply {
                                // windows
                                file = allowedExtensions.joinToString(";") { "*$it" } // e.g. '*.jpg'

                                // linux
                                setFilenameFilter { _, name ->
                                    allowedExtensions.any {
                                        name.endsWith(it)
                                    }
                                }

                                isVisible = true
                            }.files
                        }
                        value = files.firstOrNull()?.toPath()
                    }
                }
            ) {
                Text(value?.toString() ?: "Select file")
            }
        }
    }
}