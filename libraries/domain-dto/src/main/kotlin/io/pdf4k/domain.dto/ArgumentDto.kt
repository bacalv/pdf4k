package io.pdf4k.domain.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.pdf4k.domain.Argument
import io.pdf4k.domain.dto.ArgumentDto.*

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    Type(value = BooleanArgumentDto::class, name = "b"),
    Type(value = ColourArgumentDto::class, name = "c"),
    Type(value = StringArgumentDto::class, name = "s"),
    Type(value = IntArgumentDto::class, name = "i"),
    Type(value = FloatArgumentDto::class, name = "f"),
    Type(value = ObjectArgumentDto::class, name = "o"),
    Type(value = ResourceArgumentDto::class, name = "r")
)
sealed class ArgumentDto {
    abstract val name: String

    data class BooleanArgumentDto(override val name: String, val value: Boolean): ArgumentDto()
    data class ColourArgumentDto(override val name: String, val value: ColourRef): ArgumentDto()
    data class StringArgumentDto(override val name: String, val value: String): ArgumentDto()
    data class IntArgumentDto(override val name: String, val value: Int): ArgumentDto()
    data class FloatArgumentDto(override val name: String, val value: Float): ArgumentDto()
    data class ObjectArgumentDto(override val name: String, val value: List<ArgumentDto>): ArgumentDto()
    data class ResourceArgumentDto(override val name: String, val value: ResourceRef): ArgumentDto()
}

fun Argument.toDto(resourceMapBuilder: ResourceMapDto.Builder): ArgumentDto = when (this) {
    is Argument.BooleanArgument -> BooleanArgumentDto(name, value)
    is Argument.ColourArgument -> ColourArgumentDto(name, resourceMapBuilder.colourRef(value.toDto()))
    is Argument.FloatArgument -> FloatArgumentDto(name, value)
    is Argument.IntArgument -> IntArgumentDto(name, value)
    is Argument.ObjectArgument -> ObjectArgumentDto(name, value.map { it.toDto(resourceMapBuilder) })
    is Argument.ResourceArgument -> ResourceArgumentDto(name, resourceMapBuilder.resourceRef(value.toDto(resourceMapBuilder)))
    is Argument.StringArgument -> StringArgumentDto(name, value)
}

fun ArgumentDto.toDomain(resourceMap: ResourceMap): Argument = when (this) {
    is BooleanArgumentDto -> Argument.BooleanArgument(name, value)
    is ColourArgumentDto -> Argument.ColourArgument(name, resourceMap.getColour(value))
    is FloatArgumentDto -> Argument.FloatArgument(name, value)
    is IntArgumentDto -> Argument.IntArgument(name, value)
    is ObjectArgumentDto -> Argument.ObjectArgument(name, value.map { it.toDomain(resourceMap) })
    is ResourceArgumentDto -> Argument.ResourceArgument(name, resourceMap.getResourceLocation(value))
    is StringArgumentDto -> Argument.StringArgument(name, value)
}