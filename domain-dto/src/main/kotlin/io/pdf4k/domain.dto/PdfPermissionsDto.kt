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

fun PdfPermissionsDto.toDomain() = PdfPermissions(userPassword, ownerPassword, permissions.map { it.toDomain() }.toSet())

fun PdfPermissionsDto.PdfPermission.toDomain() = when (this) {
    PdfPermissionsDto.PdfPermission.Print -> PdfPermissions.PdfPermission.Print
    PdfPermissionsDto.PdfPermission.ModifyContents -> PdfPermissions.PdfPermission.ModifyContents
    PdfPermissionsDto.PdfPermission.Copy -> PdfPermissions.PdfPermission.Copy
    PdfPermissionsDto.PdfPermission.ModifyAnnotations -> PdfPermissions.PdfPermission.ModifyAnnotations
    PdfPermissionsDto.PdfPermission.FillIn -> PdfPermissions.PdfPermission.FillIn
    PdfPermissionsDto.PdfPermission.ScreenReaders -> PdfPermissions.PdfPermission.ScreenReaders
    PdfPermissionsDto.PdfPermission.Assembly -> PdfPermissions.PdfPermission.Assembly
    PdfPermissionsDto.PdfPermission.DegradedPrint -> PdfPermissions.PdfPermission.DegradedPrint
}
