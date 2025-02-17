package io.pdf4k.plugin.qrcode.server

import io.pdf4k.domain.*
import io.pdf4k.plugin.qrcode.domain.QrStyle
import io.pdf4k.plugin.qrcode.domain.QrStyle.Companion.Shape
import io.pdf4k.plugin.qrcode.domain.qrCodeProviderName
import io.pdf4k.provider.CustomResourceProvider
import io.pdf4k.provider.ResourceLocator
import qrcode.QRCodeBuilder
import qrcode.QRCodeBuilder.QRCodeShapesEnum.*
import qrcode.color.Colors
import qrcode.raw.ErrorCorrectionLevel.HIGH
import java.awt.Color
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

class QrRenderer : CustomResourceProvider {
    override val name: String = qrCodeProviderName
    override fun load(arguments: List<Argument>, resourceLocator: ResourceLocator): InputStream {
        val link = arguments.named("link").stringValue()
        val style = QrStyle(
            shape = Shape.valueOf(arguments.named("shape").stringValue()),
            colour = arguments.named("colour").colourValue(),
            background = arguments.named("background").nullableColour(),
            size = arguments.named("size").intValue(),
            logo = arguments.named("logo")?.nullableObject { obj ->
                QrStyle.Companion.Logo(
                    obj.named("location").resourceValue(),
                    obj.named("width").intValue(),
                    obj.named("height").intValue(),
                    obj.named("clearLogoArea").booleanValue()
                )
            }
        )

        return ByteArrayOutputStream().also { output ->
            val logoImage = style.logo?.location?.let { resourceLocator.load(it) }
            QRCodeBuilder(
                when (style.shape) {
                    Shape.Square -> SQUARE
                    Shape.Circle -> CIRCLE
                    Shape.RoundedSquare -> ROUNDED_SQUARE
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
        }.let {
            ByteArrayInputStream(it.toByteArray())
        }
    }

    companion object {
        private fun Color.convert(): Int = Colors.rgba(red, green, blue, alpha)
    }
}