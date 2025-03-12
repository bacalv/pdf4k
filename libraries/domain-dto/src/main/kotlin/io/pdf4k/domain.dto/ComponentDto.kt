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
    Type(value = Style::class, name = "s"),
    Type(value = Anchor::class, name = "a"),
    Type(value = Content::class, name = "c"),
    Type(value = Paragraph::class, name = "p"),
    Type(value = Phrase::class, name = "ph"),
    Type(value = Chunk::class, name = "ch"),
    Type(value = Image::class, name = "im"),
    Type(value = ItemList::class, name = "il"),
    Type(value = Link::class, name = "l"),
    Type(value = ListItem::class, name = "li"),
    Type(value = PageNumber::class, name = "pg"),
    Type(value = PageBreak::class, name = "pb"),
    Type(value = BlockBreak::class, name = "bb"),
    Type(value = Table::class, name = "t"),
    Type(value = Cell.Text::class, name = "tx"),
    Type(value = Cell.Table::class, name = "tt"),
    Type(value = Cell.Image::class, name = "ti"),
    Type(value = Cell.Composite::class, name = "tc"),
)
sealed class ComponentDto {
    data class Style(val ref: StyleRef, val children: List<ComponentDto>) : ComponentDto()

    data class Anchor(val name: String, val children: List<ComponentDto>) : ComponentDto()

    data class Content(val children: List<ComponentDto>) : ComponentDto()

    data class Paragraph(val children: List<ComponentDto>) : ComponentDto()

    data class Phrase(val children: List<ComponentDto>) : ComponentDto()

    data class Chunk(val text: String) : ComponentDto()

    data class Image(val ref: ResourceRef, val width: Float?, val height: Float?, val rotation: Float?) : ComponentDto()

    data class Link(val target: String, val phrase: Phrase) : ComponentDto()

    data class ItemList(val children: List<ComponentDto>) : ComponentDto()

    data class ListItem(val phrase: Phrase, val children: List<ComponentDto>) : ComponentDto()

    data object PageNumber : ComponentDto()

    data object PageBreak : ComponentDto()

    data object BlockBreak : ComponentDto()

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

        data class Composite(override val colSpan: Int?, override val rowSpan: Int?, val content: ComponentDto.Content) : Cell()
    }
}

fun List<Component>.toDto(resourceMapBuilder: ResourceMapDto.Builder) = map { it.toDto(resourceMapBuilder) }

fun Component.toDto(resourceMapBuilder: ResourceMapDto.Builder): ComponentDto = when (this) {
    is Component.Anchor -> Anchor(name, children.toDto(resourceMapBuilder))
    is Component.Cell.Image -> Cell.Image(colSpan, rowSpan, image.toDto(resourceMapBuilder) as Image)
    is Component.Cell.Table -> Cell.Table(colSpan, rowSpan, margin.toDto(), table.toDto(resourceMapBuilder) as Table)
    is Component.Cell.Text -> Cell.Text(rowSpan, colSpan, phrase.toDto(resourceMapBuilder) as Phrase)
    is Component.Cell.Composite -> Cell.Composite(rowSpan, colSpan, content.toDto(resourceMapBuilder) as Content)
    is Component.Chunk -> Chunk(text)
    is Component.Content -> Content(children.toDto(resourceMapBuilder))
    is Component.Image -> Image(resource.toDto(resourceMapBuilder).let(resourceMapBuilder::resourceRef), width, height, rotation)
    is Component.ItemList -> ItemList(children.map { it.toDto(resourceMapBuilder) })
    is Component.Link -> Link(target, phrase.toDto(resourceMapBuilder) as Phrase)
    is Component.ListItem -> ListItem(phrase.toDto(resourceMapBuilder) as Phrase, children.toDto(resourceMapBuilder))
    is Component.PageNumber -> PageNumber
    is Component.Paragraph -> Paragraph(children.toDto(resourceMapBuilder))
    is Component.Phrase -> Phrase(children.toDto(resourceMapBuilder))
    is Component.Style -> Style(
        styleAttributes.toDto(resourceMapBuilder).let(resourceMapBuilder::styleRef),
        children.toDto(resourceMapBuilder)
    )

    is Component.Table -> Table(
        columns, widthPercentage, weights, headerRows, extend, style?.toDto(resourceMapBuilder)
            ?.let(resourceMapBuilder::styleRef), children.toDto(resourceMapBuilder)
    )
    is Component.Break.PageBreak -> PageBreak
    is Component.Break.BlockBreak -> BlockBreak
}

fun List<ComponentDto>.toDomain(resourceMap: ResourceMap) = map { it.toDomain(resourceMap) }

fun ComponentDto.toDomain(resourceMap: ResourceMap): Component = when (this) {
    is Anchor -> Component.Anchor(name, children.toDomain(resourceMap))
    is Cell.Image -> Component.Cell.Image(colSpan, rowSpan, image.toDomain(resourceMap) as Component.Image)
    is Cell.Table -> Component.Cell.Table(
        colSpan,
        rowSpan,
        margin.toDomain(),
        table.toDomain(resourceMap) as Component.Table
    )

    is Cell.Text -> Component.Cell.Text(rowSpan, colSpan, phrase.toDomain(resourceMap) as Component.Phrase)
    is Cell.Composite -> Component.Cell.Composite(rowSpan, colSpan, content.toDomain(resourceMap) as Component.Content)
    is Chunk -> Component.Chunk(text)
    is Content -> Component.Content(children.toDomain(resourceMap))
    is Image -> Component.Image(resourceMap.getResourceLocation(ref), width, height, rotation)
    is ItemList -> Component.ItemList(children.map { it.toDomain(resourceMap) })
    is Link -> Component.Link(target, phrase.toDomain(resourceMap) as Component.Phrase)
    is ListItem -> Component.ListItem(phrase.toDomain(resourceMap) as Component.Phrase, children.toDomain(resourceMap))
    is PageNumber -> Component.PageNumber
    is Paragraph -> Component.Paragraph(children.toDomain(resourceMap))
    is Phrase -> Component.Phrase(children.toDomain(resourceMap))
    is Style -> Component.Style(resourceMap.getStyle(ref), children.toDomain(resourceMap))
    is Table -> Component.Table(
        columns,
        widthPercentage,
        weights,
        headerRows,
        extend,
        style?.let { resourceMap.getStyle(it) },
        children.toDomain(resourceMap)
    )
    is PageBreak -> Component.Break.PageBreak
    is BlockBreak -> Component.Break.BlockBreak
}