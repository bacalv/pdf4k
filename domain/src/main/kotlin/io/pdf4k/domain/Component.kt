package io.pdf4k.domain

sealed class Component(val children: List<Component>) {
    class Style(val styleAttributes: StyleAttributes, children: List<Component>) : Component(children)

    class Anchor(val name: String, children: List<Component>) : Component(children)

    class Content(children: List<Component> = emptyList()) : Component(children)

    class Paragraph(children: List<Component> = emptyList()) : Component(children)

    class Phrase(children: List<Component> = emptyList()) : Component(children)

    class Chunk(val text: String) : Component(emptyList())

    class Image(val resource: ResourceLocation, val width: Float?, val height: Float?, val rotation: Float?)
        : Component(emptyList())

    class QrCode(val link: String, val style: QrStyle) : Component(emptyList())

    class Link(val target: String, val text: String) : Component(emptyList())

    object PageNumber : Component(emptyList())

    class Table(
        val columns: Int,
        val widthPercentage: Float?,
        val weights: FloatArray?,
        val headerRows: Int,
        val extend: Boolean,
        val style: StyleAttributes?,
        children: List<Component>
    ) : Component(children)

    sealed class Cell(val colSpan: Int, val rowSpan: Int, children: List<Component> = emptyList()) : Component(children) {
        class Text(colSpan: Int, rowSpan: Int, val phrase: Phrase) : Cell(colSpan, rowSpan)

        class Table(colSpan: Int, rowSpan: Int, val margin: Margin, val table: Component.Table) : Cell(colSpan, rowSpan)

        class Image(colSpan: Int, rowSpan: Int, val image: Component.Image) : Cell(colSpan, rowSpan)

        class QrCode(colSpan: Int, rowSpan: Int, val qrCode: Component.QrCode) : Cell(colSpan, rowSpan)
    }
}
