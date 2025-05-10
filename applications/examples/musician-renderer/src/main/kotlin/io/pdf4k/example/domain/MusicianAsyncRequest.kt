package io.pdf4k.example.domain

import io.pdf4k.server.domain.AsyncPdfRequest
import io.pdf4k.server.domain.CallbackMode
import java.net.URL

class MusicianAsyncRequest(
    override val callbackMode: CallbackMode,
    override val callbackUrl: URL,
    override val payload: MusicianList
) : AsyncPdfRequest<MusicianList>