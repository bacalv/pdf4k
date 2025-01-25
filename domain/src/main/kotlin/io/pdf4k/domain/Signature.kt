package io.pdf4k.domain

import java.time.ZonedDateTime

data class Signature(
    val keyName: KeyName,
    val reason: String,
    val location: String,
    val contact: String,
    val signDate: ZonedDateTime
)
