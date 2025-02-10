package io.pdf4k.provider

import java.nio.file.Files
import java.nio.file.Path

interface TempFileFactory {
    fun createTempFile(prefix: String = "pdf4k", suffix: String = ".dat"): Path

    companion object {
        val defaultTempFileFactory = object : TempFileFactory {
            override fun createTempFile(prefix: String, suffix: String)
                    = Files.createTempFile(prefix, suffix)

        }
    }
}