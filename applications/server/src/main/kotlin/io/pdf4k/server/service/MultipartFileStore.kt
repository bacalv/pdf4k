package io.pdf4k.server.service

import org.http4k.core.MultipartEntity
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicInteger

interface MultipartFileStore {
    fun store(file: MultipartEntity.File): String
    fun get(id: String): InputStream

    companion object {
        val tempFileMultipartFileStore = object : MultipartFileStore {
            private val tempDir = Files.createTempDirectory("pdf4k-server-")
            private val fileNumber = AtomicInteger()
            override fun store(file: MultipartEntity.File): String {
                val id = fileNumber.incrementAndGet()
                val tempFile = Path.of(tempDir.toString(),"$id.dat")
                FileOutputStream(tempFile.toFile()).use {
                    file.file.content.copyTo(it)
                    file.file.content.close()
                }
                return id.toString()
            }

            override fun get(id: String): InputStream {
                val tempFile = Path.of(tempDir.toString(),"$id.dat")
                return FileInputStream(tempFile.toFile())
            }
        }
    }
}