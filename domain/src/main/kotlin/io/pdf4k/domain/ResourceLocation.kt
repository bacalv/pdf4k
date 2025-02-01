package io.pdf4k.domain

import java.net.URI

sealed interface ResourceLocation {
    data class Local(val name: String) : ResourceLocation
    sealed interface Remote : ResourceLocation {
        data class Uri(val uri: URI) : Remote
        data class Custom(val providerName: String, val name: String) : Remote
    }
}