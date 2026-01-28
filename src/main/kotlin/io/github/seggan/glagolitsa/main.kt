package io.github.seggan.glagolitsa

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.seggan.glagolitsa.ui.App
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively

@OptIn(ExperimentalPathApi::class)
fun main() {
    FileKit.init("glagolitsa")
    application {
        Window(
            onCloseRequest = {
                exitApplication()
                TEMP_DIR.deleteRecursively()
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
    "fts",
    "xisf"
)

val TEMP_DIR = Path("/home/seggan/.tmp")

fun getRandomFile(ext: String = ""): Path {
    TEMP_DIR.createDirectories()
    val filename = List(16) {
        ('a'..'z') + ('0'..'9')
    }.flatten().shuffled().take(16).joinToString("")
    return TEMP_DIR.resolve(filename + ext)
}