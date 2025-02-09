package io.pdf4k.provider

import java.io.*

fun interface TempStreamFactory {
    interface TempStream : Closeable {
        val outputStream: OutputStream
        val read: () -> InputStream
    }

    fun createTempOutputStream(): TempStream

    companion object {
        val inMemoryTempStreamFactory = TempStreamFactory {
            ByteArrayOutputStream().let { s ->
                object : TempStream {
                    private val streams = mutableListOf<Closeable>(s)
                    override val outputStream = s
                    override val read = { ByteArrayInputStream(s.toByteArray()).also { streams += it } }
                    override fun close() {
                        streams.forEach { stream ->
                            runCatching { stream.close() }.getOrNull()
                        }
                    }
                }
            }
        }
    }
}
