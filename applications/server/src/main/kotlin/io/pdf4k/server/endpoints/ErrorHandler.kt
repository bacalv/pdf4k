package io.pdf4k.server.endpoints

import io.pdf4k.domain.PdfError
import io.pdf4k.domain.PdfError.*
import org.http4k.core.Response
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.NOT_FOUND

object ErrorHandler {
    operator fun invoke(e: Throwable) = when (e) {
        is PdfError -> when (e) {
            is ClasspathResourceNotFound,
            is CustomResourceProviderNotFound,
            is FontNotFound,
            is ImageNotFound,
            is KeyNotFound,
            is KeyParseError,
            is PageTemplateNotFound -> NOT_FOUND
            is RenderingError -> INTERNAL_SERVER_ERROR
        }.let { status -> Response(status).body(e.toString()) }

        else -> Response(INTERNAL_SERVER_ERROR).body(e.toString())
    }
}