package io.pdf4k.domain

data class Pdf(
    val style: StyleAttributes?,
    val pages: List<Page>,
    val metadata: PdfMetadata,
    val signature: Signature?,
    val permissions: PdfPermissions?
)