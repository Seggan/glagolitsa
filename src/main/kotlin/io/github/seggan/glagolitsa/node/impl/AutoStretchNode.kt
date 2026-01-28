package io.github.seggan.glagolitsa.node.impl

import io.github.seggan.glagolitsa.getRandomFile
import io.github.seggan.glagolitsa.node.Node
import io.github.seggan.glagolitsa.node.Port
import io.github.seggan.glagolitsa.siril.SirilCommand
import io.github.seggan.glagolitsa.siril.callSiril
import io.github.seggan.glagolitsa.siril.sirilCommand

class AutoStretchNode : Node<AutoStretchNode>() {

    override val spec = Companion

    private val imageIn by Port.Input(
        node = this,
        name = "Image",
        type = Port.Type.Image
    )

    private val imageOut by Port.Output(
        node = this,
        name = "Stretched Image",
        type = Port.Type.Image
    )

    override suspend fun executeInternal() {
        val imageIn = imageIn.getValue()
        val outPath = getRandomFile(".fit")
        callSiril(
            sirilCommand(SirilCommand.LOAD) {
                param(imageIn)
            },
            sirilCommand(SirilCommand.AUTOSTRETCH) {
                flag("linked", true)
            },
            sirilCommand(SirilCommand.SAVE_FITS) {
                param(outPath)
            }
        )
        imageOut.provide(outPath)
    }

    companion object : Spec<AutoStretchNode>() {
        override val id = "autostretch"
        override val name = "Autostretch"
        override fun construct() = AutoStretchNode()
    }
}