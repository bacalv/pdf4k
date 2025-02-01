package io.pdf4k.provider

import io.pdf4k.domain.Font
import io.pdf4k.domain.Font.Style
import java.awt.Color
import com.lowagie.text.Font as OpenPDFFont

interface FontProvider {
    fun getFont(font: Font?, size: Float?, style: Style?, colour: Color?): OpenPDFFont
}