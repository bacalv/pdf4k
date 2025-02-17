package io.pdf4k.domain

import io.pdf4k.domain.ResourceLocation.Remote.Custom
import io.pdf4k.domain.ResourceLocation.Remote.Uri
import java.io.InputStream
import java.net.URI

sealed interface ResourceLocation {
    data class Local(val name: String) : ResourceLocation
    sealed interface Remote : ResourceLocation {
        data class Uri(val uri: URI) : Remote
        data class Custom(val providerName: String, val arguments: List<Argument>) : Remote
    }

    companion object {
        fun local(name: String) = Local(name)
        fun uri(uri: URI) = Uri(uri)
        fun uri(uri: String) = Uri(URI(uri))
        fun custom(providerName: String, vararg arguments: Argument?) = Custom(providerName, arguments.toList().mapNotNull { it })
        fun classpathResource(name: String): InputStream = ResourceLocation::class.java.getResourceAsStream(name)
            ?: throw PdfError.ClasspathResourceNotFound(name)
    }
}