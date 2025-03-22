package io.pdf4k.example.server

import io.pdf4k.domain.dto.PdfDto
import io.pdf4k.domain.dto.toDto
import io.pdf4k.example.domain.MusicianAsyncRequest
import io.pdf4k.example.domain.MusicianList
import io.pdf4k.server.domain.ServerLens.ServerJackson.auto
import io.pdf4k.server.endpoints.AsyncPdfEndpoint
import org.http4k.core.Body
import org.http4k.lens.BiDiBodyLens

@Suppress("unused")
class MusicianAsyncEndpoint : AsyncPdfEndpoint<MusicianList, MusicianAsyncRequest> {
    override val path: String = "async"
    override val summary: String = "Render musicians"
    override val requestLens: BiDiBodyLens<MusicianAsyncRequest> = Body.auto<MusicianAsyncRequest>().toLens()
    override val example = null // TODO
    override val pdfFn: (MusicianAsyncRequest) -> PdfDto = { MusicianRenderer().invoke(it.payload).toDto() }
}