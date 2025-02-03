package io.pdf4k.domain.dto

import io.pdf4k.domain.Leading

data class LeadingDto(val fixed: Float, val multiplier: Float)

fun Leading.toDto() = LeadingDto(fixed, multiplier)
fun LeadingDto.toDomain() = Leading(fixed, multiplier)