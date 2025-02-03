package io.pdf4k.domain.dto

import io.pdf4k.domain.KeyName
import io.pdf4k.domain.Signature
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

data class SignatureDto(
    val keyName: String,
    val reason: String,
    val location: String,
    val contact: String,
    val signDateUtc: Instant
)

fun Signature.toDto() = SignatureDto(keyName.name, reason, location, contact, signDate.toInstant())
fun SignatureDto.toDomain() = Signature(KeyName(keyName), reason, location, contact, ZonedDateTime.ofInstant(signDateUtc, ZoneId.systemDefault()));