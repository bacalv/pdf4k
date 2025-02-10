package io.pdf4k.dsl

import io.pdf4k.domain.Component.QrCode
import io.pdf4k.domain.QrStyle
import java.util.*

class QrBuilder(val link: String, val style: QrStyle) : ComponentBuilder<QrCode, QrBuilder> {
    override val children: MutableList<ComponentBuilder<*, *>> = Collections.unmodifiableList(emptyList())

    override fun build() = QrCode(link, style)
}