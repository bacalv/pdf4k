package io.pdf4k.domain.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.pdf4k.domain.Font

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    Type(value = FontDto.Included.Arial::class, name = "arial"),
    Type(value = FontDto.BuiltIn.Courier::class, name = "courier"),
    Type(value = FontDto.BuiltIn.Dingbats::class, name = "dingbats"),
    Type(value = FontDto.BuiltIn.Helvetica::class, name = "helvetica"),
    Type(value = FontDto.BuiltIn.Symbol::class, name = "symbol"),
    Type(value = FontDto.BuiltIn.TimesRoman::class, name = "times-roman"),
    Type(value = FontDto.Resource::class, name = "resource"),
)
sealed class FontDto {
    sealed class Included: FontDto() {
        data object Arial: Included()
    }

    sealed class BuiltIn: FontDto() {
        data object Courier: BuiltIn()
        data object Dingbats: BuiltIn()
        data object Helvetica: BuiltIn()
        data object Symbol: BuiltIn()
        data object TimesRoman: BuiltIn()
    }

    data class Resource(val ref: ResourceRef, val name: String, val type: String) : FontDto()

    enum class Style {
        Plain,
        Bold,
        Italic,
        BoldItalic
    }
}

fun Font.toDto(resourceMap: ResourceMapDto.Builder) = when (this) {
    Font.BuiltIn.Courier -> FontDto.BuiltIn.Courier
    Font.BuiltIn.Dingbats -> FontDto.BuiltIn.Dingbats
    Font.BuiltIn.Helvetica -> FontDto.BuiltIn.Helvetica
    Font.BuiltIn.Symbol -> FontDto.BuiltIn.Symbol
    Font.BuiltIn.TimesRoman -> FontDto.BuiltIn.TimesRoman
    Font.Included.Arial -> FontDto.Included.Arial
    is Font.Resource -> FontDto.Resource(resourceMap.resourceRef(resourceLocation.toDto()), name, format)
}

fun Font.Style.toDto() = when (this) {
    Font.Style.Plain -> FontDto.Style.Plain
    Font.Style.Bold -> FontDto.Style.Bold
    Font.Style.Italic -> FontDto.Style.Italic
    Font.Style.BoldItalic -> FontDto.Style.BoldItalic
}

fun FontDto.toDomain(resourceMap: ResourceMap) = when (this) {
    FontDto.BuiltIn.Courier -> Font.BuiltIn.Courier
    FontDto.BuiltIn.Dingbats -> Font.BuiltIn.Dingbats
    FontDto.BuiltIn.Helvetica -> Font.BuiltIn.Helvetica
    FontDto.BuiltIn.Symbol -> Font.BuiltIn.Symbol
    FontDto.BuiltIn.TimesRoman -> Font.BuiltIn.TimesRoman
    FontDto.Included.Arial -> Font.Included.Arial
    is FontDto.Resource -> Font.Resource(resourceMap.getResourceLocation(ref), name)
}

fun FontDto.Style.toDomain(): Font.Style = when (this) {
    FontDto.Style.Plain -> Font.Style.Plain
    FontDto.Style.Bold -> Font.Style.Bold
    FontDto.Style.Italic -> Font.Style.Italic
    FontDto.Style.BoldItalic -> Font.Style.BoldItalic
}