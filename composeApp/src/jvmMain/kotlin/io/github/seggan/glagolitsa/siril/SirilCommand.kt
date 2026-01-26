package io.github.seggan.glagolitsa.siril

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class SirilCommand(val cmd: String) {
    LOAD("load"),
    SAVE_FITS("save"),
    SAVE_BMP("savebmp"),
    SAVE_JPG("savejpg"),
    SAVE_JXL("savejxl"),
    SAVE_PNG("savepng"),
    SAVE_PNM("savepnm"),
    SAVE_TIF8("safetif8"),
    SAVE_TIF16("savetif"),
    SAFE_TIF32("safetif32"),
    ;

    inner class Builder {
        private val args = mutableListOf<String>()

        fun param(value: Any?) {
            args.add(value.toString())
        }

        fun namedParam(name: String, value: Any?) {
            args.add("-$name=$value")
        }

        fun flag(name: String, value: Boolean) {
            if (value) {
                args.add("-$name")
            }
        }

        fun build(): List<String> {
            return listOf(cmd) + args
        }
    }
}

inline fun sirilCommand(
    command: SirilCommand,
    block: SirilCommand.Builder.() -> Unit = {}
): List<String> {
    val builder = command.Builder()
    builder.block()
    return builder.build()
}

suspend fun callSiril(vararg commands: List<String>) {
    withContext(Dispatchers.IO) {
        val proc = ProcessBuilder("siril-cli", "-s", "-").start()
        proc.outputStream.bufferedWriter().use { writer ->
            writer.write("requires 1.4.0")
            writer.newLine()
            for (command in commands) {
                writer.write(command.joinToString(" "))
                writer.newLine()
            }
            writer.flush()
        }
        proc.waitFor()
        proc.errorStream.use { it.copyTo(System.err) }
        proc.inputStream.use { it.copyTo(System.out) }
    }
}