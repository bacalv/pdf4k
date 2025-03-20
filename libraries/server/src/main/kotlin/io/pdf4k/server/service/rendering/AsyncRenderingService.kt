package io.pdf4k.server.service.rendering

import io.pdf4k.domain.dto.PdfDto
import io.pdf4k.domain.dto.toDomain
import io.pdf4k.server.domain.AsyncPdfRequest
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import java.util.*
import java.util.concurrent.Executors

class AsyncRenderingService(
    private val renderingService: RenderingService,
    private val client: HttpHandler
) {
    private val executor = Executors.newVirtualThreadPerTaskExecutor()

    fun submitJob(asyncRequest: AsyncPdfRequest<*>, pdfFn: () -> PdfDto): UUID {
        val jobId = UUID.randomUUID()
        executor.submit {
            val pdf = pdfFn()
            val stream = renderingService.render(pdf.toDomain().first)
            val response = client(Request(POST, asyncRequest.callbackUrl.toString()).body(stream))
            println("${response.status}")
        }
        return jobId
    }

}