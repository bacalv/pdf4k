package io.pdf4k.server.domain

import io.pdf4k.domain.dto.PdfDto
import java.net.URL

interface AsyncPdfRequest<T> {
    val callbackMode: CallbackMode
    val callbackUrl: URL
    val payload: T
}

data class AsyncPdfDtoRequest(
    override val callbackMode: CallbackMode,
    override val callbackUrl: URL,
    override val payload: PdfDto
): AsyncPdfRequest<PdfDto>