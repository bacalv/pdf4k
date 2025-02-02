package io.pdf4k.domain.dto

import io.pdf4k.domain.PdfPermissions

data class PdfPermissionsDto(val userPassword: String, val ownerPassword: String, val permissions: Set<PdfPermission>) {
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

fun PdfPermissions.toDto() = PdfPermissionsDto(userPassword, ownerPassword, permissions.map { it.toDto() }.toSet())
fun PdfPermissions.PdfPermission.toDto() = when (this) {
    PdfPermissions.PdfPermission.Print -> PdfPermissionsDto.PdfPermission.Print
    PdfPermissions.PdfPermission.ModifyContents -> PdfPermissionsDto.PdfPermission.ModifyContents
    PdfPermissions.PdfPermission.Copy -> PdfPermissionsDto.PdfPermission.Copy
    PdfPermissions.PdfPermission.ModifyAnnotations -> PdfPermissionsDto.PdfPermission.ModifyAnnotations
    PdfPermissions.PdfPermission.FillIn -> PdfPermissionsDto.PdfPermission.FillIn
    PdfPermissions.PdfPermission.ScreenReaders -> PdfPermissionsDto.PdfPermission.ScreenReaders
    PdfPermissions.PdfPermission.Assembly -> PdfPermissionsDto.PdfPermission.Assembly
    PdfPermissions.PdfPermission.DegradedPrint -> PdfPermissionsDto.PdfPermission.DegradedPrint
}