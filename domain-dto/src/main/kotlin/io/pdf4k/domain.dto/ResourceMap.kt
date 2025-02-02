package io.pdf4k.domain.dto

data class ResourceMap(
    val colours: List<ColourDto>,
    val fonts: List<FontDto>,
    val styles: List<StyleAttributesDto>,
    val stationary: List<StationaryDto>,
    val resourceLocations: List<ResourceLocationDto>
) {
    class Builder {
        private val colours = LinkedHashSet<ColourDto>()
        private val fonts = LinkedHashSet<FontDto>()
        private val styles = LinkedHashSet<StyleAttributesDto>()
        private val resourceLocations = LinkedHashSet<ResourceLocationDto>()
        private val stationary = LinkedHashSet<StationaryDto>()

        fun colourRef(colour: ColourDto): ColourRef = colours.add(colour).let { colours.indexOf(colour) }
        fun fontRef(font: FontDto): FontRef = fonts.add(font).let { fonts.indexOf(font) }
        fun styleRef(style: StyleAttributesDto): StyleRef = styles.add(style).let { styles.indexOf(style) }
        fun stationaryRef(stationaryDto: StationaryDto): StationaryRef = stationary.add(stationaryDto).let { stationary.indexOf(stationaryDto) }
        fun resourceRef(resourceLocation: ResourceLocationDto): ResourceRef = resourceLocations.add(resourceLocation).let { resourceLocations.indexOf(resourceLocation) }

        fun build() = ResourceMap(
            colours.toList(),
            fonts.toList(),
            styles.toList(),
            stationary.toList(),
            resourceLocations.toList()
        )
    }
}

typealias ColourRef = Int
typealias FontRef = Int
typealias StyleRef = Int
typealias StationaryRef = Int
typealias ResourceRef = Int