package io.pdf4k.domain

enum class ResourceType(val directory: String, val suffix: String = "") {
    PageTemplate("stationary", ".pdf"),
    Font("fonts"),
    Image("images");

    operator fun invoke(name: String) = "/$directory/$name$suffix"
}