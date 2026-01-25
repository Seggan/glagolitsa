package io.github.seggan.glagolitsa.node.impl

import io.github.seggan.glagolitsa.node.Node
import io.github.seggan.glagolitsa.node.Parameter
import io.github.seggan.glagolitsa.node.Port

class SaveImageNode : Node {

    override val name = "Save Image"

    override val inPorts = listOf(
        Port.Input(
            name = "Image",
            type = Port.Type.IMAGE
        )
    )

    private val fileParameter = Parameter.File(".fit", ".fits")

    override val parameters = listOf(fileParameter)
}