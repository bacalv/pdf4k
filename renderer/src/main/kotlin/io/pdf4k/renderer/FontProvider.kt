package io.pdf4k.renderer

import com.lowagie.text.FontFactoryImp
import com.lowagie.text.pdf.BaseFont
import io.pdf4k.domain.Font
import io.pdf4k.domain.Font.*
import io.pdf4k.domain.Font.BuiltIn.*
import io.pdf4k.domain.Font.Custom.Resource
import io.pdf4k.domain.Font.Included.Ariel
import io.pdf4k.domain.Font.Style.*
import java.awt.Color
import com.lowagie.text.Font as OpenPDFFont

class FontProvider {
    private val factory = FontFactoryImp().also {
        it.defaultEmbedding = BaseFont.EMBEDDED
        it.defaultEncoding = BaseFont.IDENTITY_H
    }
    private val baseFontCache = mutableMapOf<String, BaseFont>()

    companion object {
        private const val FONT_RESOURCE = "fonts"
    }

    init {
        val fontsRegistered = BasicClasspathScanner.findResources(FONT_RESOURCE, setOf("ttf")).map {
            factory.register(it.toUri().toString())
        }.count()
        if (fontsRegistered == 0) throw IllegalStateException("No fonts found")
    }

    fun getFont(font: Font?, size: Float?, style: Style?, colour: Color?): OpenPDFFont {
        val defaultedFont = font ?: Ariel
        val defaultedSize = size ?: 12f
        val defaultedColour = colour ?: Color.BLACK
        val defaultedStyle = when (style ?: Plain) {
            Plain -> OpenPDFFont.NORMAL
            Bold -> OpenPDFFont.BOLD
            Italic -> OpenPDFFont.ITALIC
            BoldItalic -> OpenPDFFont.BOLDITALIC
        }
        return when (defaultedFont) {
            is BuiltIn -> {
                val family = when (defaultedFont) {
                    is Courier -> OpenPDFFont.COURIER
                    is Dingbats -> OpenPDFFont.ZAPFDINGBATS
                    is Helvetica -> OpenPDFFont.HELVETICA
                    is Symbol -> OpenPDFFont.SYMBOL
                    is TimesRoman -> OpenPDFFont.TIMES_ROMAN
                }
                OpenPDFFont(family, defaultedSize, defaultedStyle, colour)
            }

            is Included -> {
                val name = when (defaultedFont) {
                    Ariel -> "arial unicode ms"
                }
                fontWithName(name, defaultedSize, defaultedStyle, defaultedColour)
            }

            is Custom -> when (defaultedFont) {
                is Resource ->  fontWithName(defaultedFont.name, defaultedSize, defaultedStyle, defaultedColour)
            }
        }
    }

    private fun fontWithName(
        name: String,
        defaultedSize: Float,
        defaultedStyle: Int,
        defaultedColour: Color?
    ) = baseFontCache[name]?.let { baseFont ->
        OpenPDFFont(baseFont, defaultedSize, defaultedStyle, defaultedColour)
    } ?: run {
        val result = factory.getFont(name, defaultedSize, defaultedStyle, defaultedColour)
        baseFontCache[name] = result.baseFont
        result
    }
}