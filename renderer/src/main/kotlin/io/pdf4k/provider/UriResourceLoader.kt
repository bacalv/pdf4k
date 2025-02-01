package io.pdf4k.provider

import io.pdf4k.domain.PdfOutcome
import io.pdf4k.domain.asFailure
import io.pdf4k.domain.asSuccess
import io.pdf4k.renderer.PdfError
import java.io.InputStream
import java.net.URI

interface UriResourceLoader {
    fun load(uri: URI): PdfOutcome<InputStream>

    companion object {
        val defaultResourceLoader = object : UriResourceLoader {
            override fun load(uri: URI) = runCatching { uri.toURL().openStream() }
                .map { it.asSuccess() }
                .getOrElse { PdfError.RenderingError(it).asFailure() }
        }
    }
}