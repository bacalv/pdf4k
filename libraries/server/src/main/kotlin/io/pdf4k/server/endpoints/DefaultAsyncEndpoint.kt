package io.pdf4k.server.endpoints

import io.pdf4k.domain.dto.PdfDto
import io.pdf4k.server.domain.AsyncPdfDtoRequest
import io.pdf4k.server.domain.ServerLens

class DefaultAsyncEndpoint : AsyncPdfEndpoint<PdfDto, AsyncPdfDtoRequest> {
    override val path = "async/render"
    override val summary = "Render a PDF asynchronously"
    override val requestLens = ServerLens.asyncPdfDtoLens
    override val example: AsyncPdfDtoRequest? = null
    override val pdfFn: AsyncPdfDtoRequest.() -> PdfDto = { this.payload }

}