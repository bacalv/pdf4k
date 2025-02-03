package io.pdf4k.domain.dto

import io.pdf4k.domain.Block

data class BlockDto(val x: Float, val y: Float, val w: Float, val h: Float)

fun Block.toDto() = BlockDto(x, y, w, h)
fun BlockDto.toDomain() = Block(x, y, w, h)