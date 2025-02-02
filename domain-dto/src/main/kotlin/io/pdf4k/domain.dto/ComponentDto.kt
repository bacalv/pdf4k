package io.pdf4k.domain.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.pdf4k.domain.Component
import io.pdf4k.domain.dto.ComponentDto.*

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    Type(value = Style::class, name = "style"),
    Type(value = Anchor::class, name = "a"),
    Type(value = Content::class, name = "content"),
    Type(value = Paragraph::class, name = "paragraph"),
    Type(value = Phrase::class, name = "phrase"),
    Type(value = Chunk::class, name = "chunk"),
    Type(value = Image::class, name = "image"),
    Type(value = QrCode::class, name = "qr"),
    Type(value = Link::class, name = "link"),
    Type(value = PageNumber::class, name = "page-number"),
    Type(value = Table::class, name = "table"),
    Type(value = Cell.Text::class, name = "text-cell"),
    Type(value = Cell.Table::class, name = "table-cell"),
    Type(value = Cell.Image::class, name = "image-cell"),
    Type(value = Cell.QrCode::class, name = "qr-cell"),
)
sealed class ComponentDto {
    data class Style(val ref: StyleRef, val children: List<ComponentDto>) : ComponentDto()

    data class Anchor(val name: String, val children: List<ComponentDto>) : ComponentDto()

    data class Content(val children: List<ComponentDto>) : ComponentDto()

    data class Paragraph(val children: List<ComponentDto>) : ComponentDto()

    data class Phrase(val children: List<ComponentDto>) : ComponentDto()

    data class Chunk(val text: String) : ComponentDto()

    data class Image(val ref: ResourceRef, val width: Float?, val height: Float?, val rotation: Float?) : ComponentDto()

    data class QrCode(val link: String, val style: QrStyleDto) : ComponentDto()

    data class Link(val target: String, val text: String) : ComponentDto()

    data object PageNumber : ComponentDto()

    data class Table(
        val columns: Int,
        val widthPercentage: Float?,
        val weights: List<Float>?,
        val headerRows: Int?,
        val extend: Boolean?,
        val style: StyleRef?,
        val children: List<ComponentDto>
    ) : ComponentDto()

    sealed class Cell : ComponentDto() {
        abstract val colSpan: Int?
        abstract val rowSpan: Int?

        data class Text(override val colSpan: Int?, override val rowSpan: Int?, val phrase: Phrase) : Cell()

        data class Table(
            override val colSpan: Int?,
            override val rowSpan: Int?,
            val margin: MarginDto,
            val table: ComponentDto.Table
        ) : Cell()

        data class Image(override val colSpan: Int?, override val rowSpan: Int?, val image: ComponentDto.Image) : Cell()

        data class QrCode(override val colSpan: Int?, override val rowSpan: Int?, val qrCode: ComponentDto.QrCode) :
            Cell()
    }
}

fun List<Component>.toDto(resourceMapBuilder: ResourceMap.Builder) = map { it.toDto(resourceMapBuilder) }

fun Component.toDto(resourceMapBuilder: ResourceMap.Builder): ComponentDto = when (this) {
    is Component.Anchor -> Anchor(name, children.toDto(resourceMapBuilder))
    is Component.Cell.Image -> Cell.Image(colSpan, rowSpan, image.toDto(resourceMapBuilder) as Image)
    is Component.Cell.QrCode -> Cell.QrCode(colSpan, rowSpan, qrCode.toDto(resourceMapBuilder) as QrCode)
    is Component.Cell.Table -> Cell.Table(colSpan, rowSpan, margin.toDto(), table.toDto(resourceMapBuilder) as Table)
    is Component.Cell.Text -> Cell.Text(rowSpan, colSpan, phrase.toDto(resourceMapBuilder) as Phrase)
    is Component.Chunk -> Chunk(text)
    is Component.Content -> Content(children.toDto(resourceMapBuilder))
    is Component.Image -> Image(resource.toDto().let(resourceMapBuilder::resourceRef), width, height, rotation)
    is Component.Link -> Link(target, text)
    is Component.PageNumber -> PageNumber
    is Component.Paragraph -> Paragraph(children.toDto(resourceMapBuilder))
    is Component.Phrase -> Phrase(children.toDto(resourceMapBuilder))
    is Component.QrCode -> QrCode(link, style.toDto(resourceMapBuilder))
    is Component.Style -> Style(
        styleAttributes.toDto(resourceMapBuilder).let(resourceMapBuilder::styleRef),
        children.toDto(resourceMapBuilder)
    )

    is Component.Table -> Table(
        columns, widthPercentage, weights, headerRows, extend, style?.toDto(resourceMapBuilder)
            ?.let(resourceMapBuilder::styleRef), children.toDto(resourceMapBuilder)
    )
}