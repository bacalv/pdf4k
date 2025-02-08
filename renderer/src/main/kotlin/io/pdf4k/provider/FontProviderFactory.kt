package io.pdf4k.provider

import com.lowagie.text.FontFactoryImp
import com.lowagie.text.pdf.BaseFont
import io.pdf4k.domain.Font
import io.pdf4k.domain.Font.*
import io.pdf4k.domain.Font.BuiltIn.*
import io.pdf4k.domain.Font.Included.Arial
import io.pdf4k.domain.Font.Style.*
import io.pdf4k.renderer.PdfError.Companion.PdfErrorException
import io.pdf4k.renderer.PdfError.FontNotFound
import io.pdf4k.util.BasicClasspathScanner
import java.awt.Color
import java.io.FileOutputStream
import com.lowagie.text.Font as OpenPDFFont

class FontProviderFactory(private val tempFileFactory: TempFileFactory) {
    companion object {
        private const val FONT_RESOURCE = "fonts"
    }

    private val factory = FontFactoryImp().also {
        it.defaultEmbedding = BaseFont.EMBEDDED
        it.defaultEncoding = BaseFont.IDENTITY_H
    }

    private val baseFontCache = mutableMapOf<String, BaseFont>()

    init {
        BasicClasspathScanner.findResources(FONT_RESOURCE, setOf("ttf")).forEach {
            println("REGISTERING FONT $it")
            factory.register(it.toUri().toString())
        }
    }

    fun newFontProvider(resourceLocator: ResourceLocator) = object : FontProvider {
        private val customFontFactory = FontFactoryImp().also {
            it.defaultEmbedding = BaseFont.EMBEDDED
            it.defaultEncoding = BaseFont.IDENTITY_H
        }

        override fun getFont(font: Font?, size: Float?, style: Style?, colour: Color?): com.lowagie.text.Font {
            val defaultedFont = font ?: Arial
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
                        is Arial -> "arial unicode ms"
                    }
                    factory.fontWithName(name, defaultedSize, defaultedStyle, defaultedColour)
                }

                is Resource -> resourceLocator.load(defaultedFont.resourceLocation)
                    .map { inputStream ->
                        tempFileFactory.createTempFile(suffix = ".${defaultedFont.type}").let { tempFile ->
                            inputStream.copyTo(FileOutputStream(tempFile.toFile()))
                            customFontFactory.register(tempFile.toUri().toString())
                            customFontFactory.fontWithName(
                                defaultedFont.name,
                                defaultedSize,
                                defaultedStyle,
                                defaultedColour
                            )
                        }
                    }.getOrNull() ?: throw PdfErrorException(FontNotFound(defaultedFont.resourceLocation.toString()))
            }

        }

        private fun FontFactoryImp.fontWithName(
            name: String,
            defaultedSize: Float,
            defaultedStyle: Int,
            defaultedColour: Color?
        ) = baseFontCache[name]?.let { baseFont ->
            OpenPDFFont(baseFont, defaultedSize, defaultedStyle, defaultedColour)
        } ?: run {
            val result = getFont(name, defaultedSize, defaultedStyle, defaultedColour)
            if (result.baseFont == null) {
                throw PdfErrorException(FontNotFound(name))
            }
            baseFontCache[name] = result.baseFont
            result
        }
    }
}