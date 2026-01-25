package io.github.seggan.glagolitsa.node.impl

import io.github.seggan.glagolitsa.node.Parameter
import io.github.seggan.glagolitsa.node.Node
import io.github.seggan.glagolitsa.node.Port

class LoadImageNode : Node {

    override val name = "Load Image"

    override val outPorts = listOf(
        Port.Output(
            name = "Image",
            type = Port.Type.IMAGE
        )
    )

    private val fileParameter = Parameter.File(".fit", ".fits")

    override val parameters = listOf(fileParameter)
}