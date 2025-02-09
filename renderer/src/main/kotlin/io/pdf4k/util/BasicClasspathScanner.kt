package io.pdf4k.util

import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension

object BasicClasspathScanner {
    fun findResources(baseFolder: String, extensions: Set<String>) = System.getProperty("java.class.path")
        .split(":")
        .map { classpathEntry ->
            try {
                if (Files.isDirectory(Path.of(classpathEntry))) {
                    Files.walk(Path.of(classpathEntry, baseFolder)).filter { it.extension in extensions }
                        .toList()
                } else {
                    FileSystems.newFileSystem(URI.create("jar:file:$classpathEntry"), emptyMap<String, String>())
                        .use { fileSystem ->
                            Files.walk(fileSystem.getPath(baseFolder)).filter { it.extension in extensions }.toList()
                        }
                }
            } catch (e: Exception) {
                emptyList()
            }
        }.flatten()
}