package io.github.seggan.glagolitsa.node.impl

import io.github.seggan.glagolitsa.ASTRO_IMAGE_TYPE
import io.github.seggan.glagolitsa.getRandomFile
import io.github.seggan.glagolitsa.node.Node
import io.github.seggan.glagolitsa.node.NodeException
import io.github.seggan.glagolitsa.node.Parameter
import io.github.seggan.glagolitsa.node.Port
import io.github.seggan.glagolitsa.siril.SirilCommand
import io.github.seggan.glagolitsa.siril.callSiril
import io.github.seggan.glagolitsa.siril.sirilCommand
import kotlin.io.path.exists

class LoadImageNode : Node<LoadImageNode>() {

    override val spec = Companion

    private val imageOut by Port.Output(
        node = this,
        name = "Image",
        type = Port.Type.Image
    )

    private val file by Parameter.FilePicker(ASTRO_IMAGE_TYPE)

    override suspend fun executeInternal() {
        val out = getRandomFile(".fit")
        val file = file ?: throw NodeException("No file selected")
        if (!file.exists()) {
            throw NodeException("File does not exist: $file")
        }
        callSiril(
            sirilCommand(SirilCommand.LOAD),
            sirilCommand(SirilCommand.SAVE_FITS)
        )
        imageOut.provide(out)
    }

    companion object : Spec<LoadImageNode>() {
        override val id = "load-image"
        override val name = "Load Image"

        override fun construct() = LoadImageNode()
    }
}