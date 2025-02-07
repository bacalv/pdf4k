package io.pdf4k.domain.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.pdf4k.domain.ResourceLocation
import io.pdf4k.domain.dto.ResourceLocationDto.*
import java.net.URI

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    Type(value = Local::class, name = "local"),
    Type(value = Uri::class, name = "uri"),
    Type(value = Custom::class, name = "custom")
)
sealed class ResourceLocationDto {
    data class Local(val name: String) : ResourceLocationDto()
    data class Uri(val uri: String) : ResourceLocationDto()
    data class Custom(val providerName: String, val name: String) : ResourceLocationDto()
}

fun ResourceLocation.toDto() = when (this) {
    is ResourceLocation.Local -> Local(name)
    is ResourceLocation.Remote.Custom -> Custom(providerName, name)
    is ResourceLocation.Remote.Uri -> Uri(uri.toString())
}

fun ResourceLocationDto.toDomain() = when (this) {
    is Custom -> ResourceLocation.Remote.Custom(providerName, name)
    is Local -> ResourceLocation.Local(name)
    is Uri -> ResourceLocation.Remote.Uri(URI(uri))
}