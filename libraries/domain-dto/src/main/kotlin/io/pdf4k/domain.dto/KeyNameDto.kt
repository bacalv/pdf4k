package io.pdf4k.domain.dto

import io.pdf4k.domain.KeyName

data class KeyNameDto(val name: String)

fun KeyName.toDto() = KeyNameDto(name)