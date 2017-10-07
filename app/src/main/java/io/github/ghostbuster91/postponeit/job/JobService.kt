package io.github.ghostbuster91.postponeit.job

import io.github.ghostbuster91.postponeit.AlarmManagerService
import io.github.ghostbuster91.postponeit.AlarmManagerServiceImpl
import io.github.ghostbuster91.postponeit.contextProvider
import java.util.*

interface JobService {
    fun createJob(timeInMillis: Long, smsText: String, smsTextNumber: String)
    fun cancelJob(jobToCancelId: String)
    fun findJob(delayedJobId: String): DelayedJob
    fun updateJob(delayedJob: DelayedJob)
    fun getJobs(filter: JobFilter = JobFilter.ALL): List<DelayedJob>
}

private class JobServiceImpl(private val alarmManagerService: AlarmManagerService,
                             private val jobRepository: JobRepository) : JobService {

    override fun createJob(timeInMillis: Long, smsText: String, smsTextNumber: String) {
        val id = UUID.randomUUID().toString()
        val delayedJob = DelayedJob(id = id, text = smsText, number = smsTextNumber, timeInMillis = timeInMillis)
        alarmManagerService.createAlarm(delayedJob)
        jobRepository.addJob(delayedJob)
    }

    override fun cancelJob(jobToCancelId: String) {
        val delayedJob1 = jobRepository.getJobs().first { it.id == jobToCancelId }
        jobRepository.updateJob(delayedJob1.copy(status = DelayedJobStatus.CANCELED))
    }

    override fun findJob(delayedJobId: String): DelayedJob = jobRepository.getJobs().first { it.id == delayedJobId }

    override fun updateJob(delayedJob: DelayedJob) {
        jobRepository.updateJob(delayedJob)
    }

    override fun getJobs(filter: JobFilter) = filter.apply(jobRepository.getJobs())
}

val jobServiceProvider: JobService by lazy { JobServiceImpl(AlarmManagerServiceImpl(contextProvider()), jobRepositoryProvider) }