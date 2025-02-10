package io.pdf4k.domain

data class PdfPermissions(val userPassword: String, val ownerPassword: String, val permissions: Set<PdfPermission>) {
    enum class PdfPermission {
        Print,
        ModifyContents,
        Copy,
        ModifyAnnotations,
        FillIn,
        ScreenReaders,
        Assembly,
        DegradedPrint
    }
}
