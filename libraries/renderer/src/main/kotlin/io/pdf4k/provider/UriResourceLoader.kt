package io.pdf4k.provider

import io.pdf4k.domain.PdfError
import java.io.InputStream
import java.net.URI

interface UriResourceLoader {
    fun load(uri: URI): InputStream

    companion object {
        val defaultResourceLoader = object : UriResourceLoader {
            override fun load(uri: URI) = runCatching { uri.toURL().openStream() }
                .getOrElse { throw PdfError.RenderingError(it) }
        }
    }
}