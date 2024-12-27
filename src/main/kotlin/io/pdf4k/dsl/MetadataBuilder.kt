package io.pdf4k.dsl

import io.pdf4k.domain.PdfMetadata

class MetadataBuilder {
    var title: String? = null
    var author: String? = null
    var subject: String? = null
    var keywords: String? = null
    var creator: String? = null
    var producer: String? = null

    fun build() = PdfMetadata(title, author, subject, keywords, creator, producer)
}
