package io.pdf4k.renderer

import io.pdf4k.domain.QrStyle
import io.pdf4k.domain.QrStyle.Companion.Shape.*
import qrcode.QRCodeBuilder
import qrcode.QRCodeBuilder.QRCodeShapesEnum.*
import qrcode.color.Colors
import qrcode.raw.ErrorCorrectionLevel.HIGH
import java.awt.Color
import java.io.ByteArrayOutputStream

object QrRenderer {
    fun render(link: String, style: QrStyle): ByteArray {
        val output = ByteArrayOutputStream()
        QRCodeBuilder(
            when (style.shape) {
                Square -> SQUARE
                Circle -> CIRCLE
                RoundedSquare -> ROUNDED_SQUARE
            }
        ).withColor(style.colour.convert()).also { builder ->
            style.background?.convert()?.let { builder.withBackgroundColor(it) }
        }
            .withSize(style.size)
            .withErrorCorrectionLevel(HIGH).also { builder ->
                style.logo?.let { logo ->
                    ClassLoader.getSystemResourceAsStream("images/${logo.resource}")?.let {
                        builder.withLogo(
                            logo = it.readBytes(),
                            width = logo.width,
                            height = logo.height,
                            clearLogoArea = logo.clearLogoArea
                        )
                    }
                }
            }
            .build(link)
            .render()
            .writeImage(output)
        return output.toByteArray()
    }
}

private fun Color.convert(): Int = Colors.rgba(red, green, blue, alpha)
