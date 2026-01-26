package io.github.seggan.glagolitsa

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.seggan.glagolitsa.node.Node
import io.github.seggan.glagolitsa.ui.App
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively

@OptIn(ExperimentalPathApi::class)
fun main() {
    FileKit.init("glagolitsa")
    application {
        Window(
            onCloseRequest = {
                exitApplication()
                Node.TEMP_DIR.deleteRecursively()
            },
            title = "Glagolitsa",
        ) {
            App()
        }
    }
}

val ASTRO_IMAGE_TYPE = FileKitType.File(
    "avif",
    "bmp",
    "heic",
    "heif",
    "jpeg",
    "jpg",
    "jxl",
    "png",
    "tif",
    "tiff",
    "pbm",
    "pgm",
    "ppm",
    "fit",
    "fits",
    "fts"
)