package io.github.seggan.glagolitsa.node.impl

import io.github.seggan.glagolitsa.ASTRO_IMAGE_TYPE
import io.github.seggan.glagolitsa.node.Node
import io.github.seggan.glagolitsa.node.NodeException
import io.github.seggan.glagolitsa.node.Parameter
import io.github.seggan.glagolitsa.node.Port
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.io.path.copyTo
import kotlin.io.path.exists

class LoadImageNode : Node() {

    override val name = "Load Image"

    private val imageOut by Port.Output(
        node = this,
        name = "Image",
        type = Port.Type.Image
    )

    private val file by Parameter.FilePicker(ASTRO_IMAGE_TYPE)

    override suspend fun executeInternal() {
        val out = getRandomFile()
        val file = file ?: throw NodeException("No file selected")
        if (!file.exists()) {
            throw NodeException("File does not exist: $file")
        }
        withContext(Dispatchers.IO) {
            file.copyTo(out)
        }
        imageOut.provide(out)
    }
}