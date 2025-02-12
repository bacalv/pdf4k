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
    Type(value = FontDto.Arial::class, name = "arial"),
    Type(value = FontDto.Courier::class, name = "courier"),
    Type(value = FontDto.Dingbats::class, name = "dingbats"),
    Type(value = FontDto.Helvetica::class, name = "helvetica"),
    Type(value = FontDto.Symbol::class, name = "symbol"),
    Type(value = FontDto.TimesRoman::class, name = "times-roman"),
    Type(value = FontDto.Resource::class, name = "resource"),
)
sealed class FontDto {
    data object Arial: FontDto()
    data object Courier: FontDto()
    data object Dingbats: FontDto()
    data object Helvetica: FontDto()
    data object Symbol: FontDto()
    data object TimesRoman: FontDto()

    data class Resource(val ref: ResourceRef, val name: String, val format: String) : FontDto()

    enum class Style {
        Plain,
        Bold,
        Italic,
        BoldItalic
    }
}

fun Font.toDto(resourceMap: ResourceMapDto.Builder) = when (this) {
    Font.BuiltIn.Courier -> FontDto.Courier
    Font.BuiltIn.Dingbats -> FontDto.Dingbats
    Font.BuiltIn.Helvetica -> FontDto.Helvetica
    Font.BuiltIn.Symbol -> FontDto.Symbol
    Font.BuiltIn.TimesRoman -> FontDto.TimesRoman
    Font.Included.Arial -> FontDto.Arial
    is Font.Resource -> FontDto.Resource(resourceMap.resourceRef(resourceLocation.toDto()), name, format)
}

fun Font.Style.toDto() = when (this) {
    Font.Style.Plain -> FontDto.Style.Plain
    Font.Style.Bold -> FontDto.Style.Bold
    Font.Style.Italic -> FontDto.Style.Italic
    Font.Style.BoldItalic -> FontDto.Style.BoldItalic
}

fun FontDto.toDomain(resourceMap: ResourceMap) = when (this) {
    FontDto.Courier -> Font.BuiltIn.Courier
    FontDto.Dingbats -> Font.BuiltIn.Dingbats
    FontDto.Helvetica -> Font.BuiltIn.Helvetica
    FontDto.Symbol -> Font.BuiltIn.Symbol
    FontDto.TimesRoman -> Font.BuiltIn.TimesRoman
    FontDto.Arial -> Font.Included.Arial
    is FontDto.Resource -> Font.Resource(resourceMap.getResourceLocation(ref), name)
}

fun FontDto.Style.toDomain(): Font.Style = when (this) {
    FontDto.Style.Plain -> Font.Style.Plain
    FontDto.Style.Bold -> Font.Style.Bold
    FontDto.Style.Italic -> Font.Style.Italic
    FontDto.Style.BoldItalic -> Font.Style.BoldItalic
}