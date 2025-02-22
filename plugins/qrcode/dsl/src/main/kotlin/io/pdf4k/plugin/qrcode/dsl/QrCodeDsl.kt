package io.pdf4k.plugin.qrcode.dsl

import io.pdf4k.domain.ResourceLocation.Companion.custom
import io.pdf4k.domain.StyleAttributes
import io.pdf4k.domain.toArgument
import io.pdf4k.dsl.AnyTableBuilder
import io.pdf4k.plugin.qrcode.domain.QrStyle
import io.pdf4k.plugin.qrcode.domain.qrCodeProviderName

fun AnyTableBuilder.qrCodeCell(
    link: String,
    qrStyle: QrStyle,
    style: StyleAttributes? = null,
    colSpan: Int = 1,
    rowSpan: Int = 1,
    width: Float = 50f,
    height: Float = 50f
) {
    if (style != null) {
        style(style) {
            qrCodeCell(link, qrStyle, null, colSpan, rowSpan, width, height)
        }
    } else {
        val customResource = custom(
            providerName = qrCodeProviderName,
            link.toArgument("link"),
            qrStyle.shape.name.toArgument("shape"),
            qrStyle.colour.toArgument("colour"),
            qrStyle.background.toArgument("background"),
            qrStyle.size.toArgument("size"),
            qrStyle.logo.toArgument("logo") { logo ->
                listOf(
                    logo.location.toArgument("location"),
                    logo.width.toArgument("width"),
                    logo.height.toArgument("height"),
                    logo.clearLogoArea.toArgument("clearLogoArea")
                )
            }
        )
        imageCell(customResource, null, colSpan, rowSpan, width, height)
    }
}