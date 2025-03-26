package io.pdf4k.server.domain

sealed interface PdfResult {
    object Success: PdfResult

    object Failure: PdfResult
}