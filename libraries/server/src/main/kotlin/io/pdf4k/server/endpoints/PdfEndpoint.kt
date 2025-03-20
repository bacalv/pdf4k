package io.pdf4k.server.endpoints

import io.pdf4k.domain.Pdf
import io.pdf4k.domain.dto.PdfDto
import io.pdf4k.server.domain.AsyncPdfRequest
import org.http4k.contract.RouteMetaDsl
import org.http4k.core.Request
import org.http4k.lens.BiDiBodyLens

interface PdfEndpoint<T : Any, P> {
    val path: String
    val summary: String get() = "PDF"
    val requestLens: BiDiBodyLens<T>
    val example: T?
    val pdfFn: (T) -> P
    fun pdfFor(request: Request) = pdfFn(requestLens(request))
    fun receiving(meta: RouteMetaDsl) {
        example?.let { meta.receiving(requestLens to it) }
            ?: meta.receiving(requestLens)
    }
}

interface SyncPdfEndpoint<T: Any> : PdfEndpoint<T, Pdf>

interface AsyncPdfEndpoint<T: Any, R : AsyncPdfRequest<T>> : PdfEndpoint<R, PdfDto>


