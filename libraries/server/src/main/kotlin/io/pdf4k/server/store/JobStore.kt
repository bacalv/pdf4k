package io.pdf4k.server.store

import io.pdf4k.server.domain.JobStatus
import io.pdf4k.server.domain.PdfJob
import java.util.*

interface JobStore {
    fun submitJob(job: PdfJob): UUID

    fun findJobs(status: JobStatus): List<UUID>

    fun withJob(id: UUID, expectedStatus: JobStatus, block: (PdfJob) -> JobStatus): Boolean
}