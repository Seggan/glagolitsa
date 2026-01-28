package io.github.seggan.glagolitsa.node.impl

import io.github.seggan.glagolitsa.node.Node
import io.github.seggan.glagolitsa.node.NodeException
import io.github.seggan.glagolitsa.node.Parameter
import io.github.seggan.glagolitsa.node.Port
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.io.path.copyTo
import kotlin.io.path.createDirectories

class SaveFitsNode : Node<SaveFitsNode>() {

    override val spec = Companion

    private val imageIn by Port.Input(
        node = this,
        name = "Image",
        type = Port.Type.Image
    )

    private val file by Parameter.SaveFilePicker("image", "fits")

    override suspend fun executeInternal() {
        val origin = imageIn.getValue()
        val destination = file ?: throw NodeException("No file selected")
        withContext(Dispatchers.IO) {
            destination.parent?.createDirectories()
            origin.copyTo(destination, overwrite = true)
        }
    }

    companion object : Spec<SaveFitsNode>() {
        override val id = "save-fits"
        override val name = "Save Image as FITS"
        override fun construct() = SaveFitsNode()
    }
}