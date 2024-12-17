package io.pdf4k.domain

sealed class Font {
    sealed class BuiltIn: Font() {
        data object Ariel: BuiltIn()
    }

    enum class Style {
        Plain,
        Bold,
        Italic,
        BoldItalic
    }
}