package io.pdf4k.renderer

import io.pdf4k.domain.QrStyle
import io.pdf4k.domain.QrStyle.Companion.Shape.*
import io.pdf4k.provider.ResourceLocators
import qrcode.QRCodeBuilder
import qrcode.QRCodeBuilder.QRCodeShapesEnum.*
import qrcode.color.Colors
import qrcode.raw.ErrorCorrectionLevel.HIGH
import java.awt.Color
import java.io.ByteArrayOutputStream

class QrRenderer(private val resourceLocators: ResourceLocators) {
    fun render(link: String, style: QrStyle): ByteArray =
        ByteArrayOutputStream().let { output ->
            val logoImage = style.logo?.resource?.let { resourceLocators.imageResourceLocator.load(it) }
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
                        logoImage?.let {
                            builder.withLogo(
                                logo = it.readAllBytes(),
                                width = logo.width,
                                height = logo.height,
                                clearLogoArea = logo.clearLogoArea
                            )
                        }
                    }
                }
                .build(link)
                .render()
                .writeImage(output).let { output.toByteArray() }
        }

    companion object {
        private fun Color.convert(): Int = Colors.rgba(red, green, blue, alpha)
    }
}