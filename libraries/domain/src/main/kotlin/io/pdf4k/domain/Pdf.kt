package io.pdf4k.domain

data class Pdf(
    val style: StyleAttributes?,
    val sections: List<Section>,
    val metadata: PdfMetadata,
    val signature: Signature?,
    val permissions: PdfPermissions?
)