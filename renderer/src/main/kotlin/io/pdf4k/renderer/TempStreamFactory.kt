package io.pdf4k.renderer

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

fun interface TempStreamFactory {
    data class TempStream(val outputStream: OutputStream, val read: () -> InputStream)
    fun createTempOutputStream(): TempStream

    companion object {
        val inMemoryTempStreamFactory = TempStreamFactory { ByteArrayOutputStream().let { s ->
            TempStream(s) { ByteArrayInputStream(s.toByteArray()) }
        } }
    }
}
