package io.github.seggan.glagolitsa.node.impl

import io.github.seggan.glagolitsa.node.Input
import io.github.seggan.glagolitsa.node.Node
import io.github.seggan.glagolitsa.node.Port

class LoadImage : Node {

    override val name = "Load Image"

    override val inPorts = listOf(
        Port.Output(
            name = "Image",
            type = Port.Type.IMAGE
        )
    )

    override val outPorts = listOf(
        Port.Output(
            name = "Image",
            type = Port.Type.IMAGE
        )
    )

    private val fileInput = Input.File(".fit", ".fits")

    override val inputs = listOf(fileInput)
}