package io.pdf4k.domain

sealed class Font {
    sealed class Included: Font() {
        data object Ariel: Included()
    }

    sealed class BuiltIn: Font() {
        data object Courier: BuiltIn()
        data object Dingbats: BuiltIn()
        data object Helvetica: BuiltIn()
        data object Symbol: BuiltIn()
        data object TimesRoman: BuiltIn()
    }

    sealed class Custom: Font() {
        data class Resource(val name: String) : Custom()
    }

    enum class Style {
        Plain,
        Bold,
        Italic,
        BoldItalic
    }
}