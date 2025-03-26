package io.pdf4k.server.domain

import io.pdf4k.domain.dto.PdfDto
import java.net.URL

data class PdfJob(
    val pdf: PdfDto,
    val callbackMode: CallbackMode,
    val callbackUrl: URL
)