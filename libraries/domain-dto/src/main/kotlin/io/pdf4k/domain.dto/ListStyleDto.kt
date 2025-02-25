package io.pdf4k.domain.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.pdf4k.domain.ListStyle
import io.pdf4k.domain.dto.ListStyleDto.Numbered
import io.pdf4k.domain.dto.ListStyleDto.Symbol

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    Type(value = Symbol::class, name = "s"),
    Type(value = Numbered::class, name = "n"),
)
sealed interface ListStyleDto {
    data class Symbol(val symbol: String?) : ListStyleDto
    data class Numbered(val startAt: Int?) : ListStyleDto
}

fun ListStyle.toDto() = when (this) {
    is ListStyle.Numbered -> Numbered(startAt)
    is ListStyle.Symbol -> Symbol(symbol)
}

fun ListStyleDto.toDomain() = when (this) {
    is Numbered -> ListStyle.Numbered(startAt)
    is Symbol -> ListStyle.Symbol(symbol)
}