package io.github.seggan.glagolitsa.node.impl

import androidx.compose.ui.graphics.painter.BitmapPainter
import io.github.seggan.glagolitsa.getRandomFile
import io.github.seggan.glagolitsa.node.Node
import io.github.seggan.glagolitsa.node.Output
import io.github.seggan.glagolitsa.node.Parameter
import io.github.seggan.glagolitsa.node.Port
import io.github.seggan.glagolitsa.siril.SirilCommand
import io.github.seggan.glagolitsa.siril.callSiril
import io.github.seggan.glagolitsa.siril.sirilCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.decodeToImageBitmap
import kotlin.io.path.Path
import kotlin.io.path.readBytes

class PreviewNode : Node<PreviewNode>() {

    override val spec = Companion

    private val image by Port.Input(
        node = this,
        name = "Image",
        type = Port.Type.Image
    )

    private val quality by Parameter.FloatSlider(
        label = "Quality",
        initialValue = 0.5f,
        range = 0.20001f..0.99999f
    )

    private val render by Output.Image()

    override suspend fun executeInternal() {
        val inputFile = image.getValue()
        val png = getRandomFile()
        callSiril(
            sirilCommand(SirilCommand.LOAD) {
                param(inputFile)
            },
            sirilCommand(SirilCommand.RESAMPLE) {
                param(quality)
            },
            sirilCommand(SirilCommand.SAVE_PNG) {
                param(png)
            }
        )
        val renderImage = withContext(Dispatchers.IO) {
            val actualFile = Path("$png.png")
            actualFile.readBytes().decodeToImageBitmap()
        }
        render.provide(BitmapPainter(renderImage))
    }

    companion object : Spec<PreviewNode>() {
        override val id = "preview"
        override val name = "Preview Image"
        override fun construct() = PreviewNode()
    }
}