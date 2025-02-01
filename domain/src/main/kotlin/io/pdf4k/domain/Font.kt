package io.pdf4k.domain

sealed class Font {
    sealed class Included: Font() {
        data object Arial: Included()
    }

    sealed class BuiltIn: Font() {
        data object Courier: BuiltIn()
        data object Dingbats: BuiltIn()
        data object Helvetica: BuiltIn()
        data object Symbol: BuiltIn()
        data object TimesRoman: BuiltIn()
    }

    data class Resource(val resourceLocation: ResourceLocation, val name: String, val type: String = "ttf") : Font()

    enum class Style {
        Plain,
        Bold,
        Italic,
        BoldItalic
    }
}