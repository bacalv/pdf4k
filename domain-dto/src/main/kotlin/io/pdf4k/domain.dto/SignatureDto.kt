package io.pdf4k.domain.dto

import io.pdf4k.domain.Signature
import java.time.Instant

data class SignatureDto(
    val keyName: KeyNameDto,
    val reason: String,
    val location: String,
    val contact: String,
    val signDateUtc: Instant
)

fun Signature.toDto() = SignatureDto(keyName.toDto(), reason, location, contact, signDate.toInstant())