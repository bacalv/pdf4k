package io.pdf4k.server.service.rendering

import io.pdf4k.domain.dto.PdfDto
import io.pdf4k.domain.dto.toDomain
import io.pdf4k.server.domain.AsyncPdfRequest
import io.pdf4k.server.domain.JobStatus.*
import io.pdf4k.server.domain.PdfJob
import io.pdf4k.server.store.JobStore
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import java.util.*
import java.util.concurrent.Executors

class AsyncRenderingService(
    private val renderingService: RenderingService,
    private val client: HttpHandler,
    private val store: JobStore,
    private val sleepTimeMs: Long = 20
) {
    private val executor = Executors.newVirtualThreadPerTaskExecutor()
    @Volatile
    private var running = false

    fun submitJob(asyncRequest: AsyncPdfRequest<*>, pdfFn: () -> PdfDto): UUID {
        return store.submitJob(PdfJob(pdfFn(), asyncRequest.callbackMode, asyncRequest.callbackUrl))
    }

    fun start() {
        executor.submit {
            running = true
            while (running) {
                processJobs()
                Thread.sleep(sleepTimeMs)
            }
        }
    }

    private fun processJobs() {
        store.findJobs(Pending).forEach { jobId ->
            executor.submit {
                store.withJob(jobId, Pending) { job ->
                    val stream = renderingService.render(job.pdf.toDomain().first)
                    val response = client(Request(POST, job.callbackUrl.toString()).body(stream))
                    if (response.status.successful) Complete else Error
                }
            }
        }
    }

    fun stop() {
        running = false
    }
}