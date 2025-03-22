package io.pdf4k.example.server

import io.pdf4k.example.domain.MusicianList
import io.pdf4k.server.domain.ServerLens.ServerJackson.auto
import io.pdf4k.server.endpoints.SyncPdfEndpoint
import io.pdf4k.testing.domain.Musician.Companion.musicians
import org.http4k.core.Body
import org.http4k.lens.BiDiBodyLens

@Suppress("unused")
class MusicianSyncEndpoint : SyncPdfEndpoint<MusicianList> {
    override val path: String = "musicians"
    override val summary: String = "Render musicians"
    override val requestLens: BiDiBodyLens<MusicianList> = Body.auto<MusicianList>().toLens()
    override val example = MusicianList(musicians)
    override val pdfFn = MusicianRenderer()
}