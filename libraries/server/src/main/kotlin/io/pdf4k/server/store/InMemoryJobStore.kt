package io.pdf4k.server.store

import io.pdf4k.server.domain.JobStatus
import io.pdf4k.server.domain.JobStatus.Pending
import io.pdf4k.server.domain.PdfJob
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentSkipListSet

class InMemoryJobStore: JobStore {
    private val store = ConcurrentHashMap<UUID, Pair<PdfJob, JobStatus>>()
    private val locks = ConcurrentSkipListSet<UUID>()

    override fun submitJob(job: PdfJob): UUID = UUID.randomUUID().also { id ->
        store += id to (job to Pending)
    }

    override fun findJobs(status: JobStatus) = store.filter { (_, jobAndStatus) ->
        jobAndStatus.let { (_, s) ->
            status == s
        }
    }.keys.toList()

    override fun withJob(id: UUID, expectedStatus: JobStatus, block: (PdfJob) -> JobStatus): Boolean {
        if (!acquireLock(id)) return false
        store[id]?.let { (job, status) ->
            takeIf { status == expectedStatus }?.let {
                store[id] = job to block(job)
            } ?: return false
        }
        releaseLock(id)
        return true
    }

    @Synchronized
    private fun acquireLock(id: UUID): Boolean {
        if (!locks.contains(id)) {
            locks += id
            return true
        } else {
            return false
        }
    }

    private fun releaseLock(id: UUID) {
        locks.remove(id)
    }
}