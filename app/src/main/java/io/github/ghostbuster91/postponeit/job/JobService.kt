package io.github.ghostbuster91.postponeit.job

import io.github.ghostbuster91.postponeit.AlarmManagerService
import io.github.ghostbuster91.postponeit.AlarmManagerServiceImpl
import io.github.ghostbuster91.postponeit.contextProvider

interface JobService {
    fun createJob(timeInMillis: Long, smsText: String, smsTextNumber: String)
    fun cancelJob(jobToCancelId: Int)
    fun findJob(delayedJobId: Int): DelayedJob
    fun updateJob(delayedJob: DelayedJob)
    fun getJobs(filter: JobFilter = JobFilter.ALL): List<DelayedJob>
}

private class JobServiceImpl(private val alarmManagerService: AlarmManagerService,
                             private val jobRepository: JobRepository) : JobService {

    override fun createJob(timeInMillis: Long, smsText: String, smsTextNumber: String) {
        val id = Math.abs(java.util.Random().nextInt())
        val delayedJob = DelayedJob(id = id, text = smsText, number = smsTextNumber, timeInMillis = timeInMillis)
        alarmManagerService.createAlarm(delayedJob)
        jobRepository.addJob(delayedJob)
    }

    override fun cancelJob(jobToCancelId: Int) {
        val delayedJob1 = jobRepository.getJobs().first { it.id == jobToCancelId }
        jobRepository.updateJob(delayedJob1.copy(status = DelayedJobStatus.CANCELED))
    }

    override fun findJob(delayedJobId: Int): DelayedJob = jobRepository.getJobs().first { it.id == delayedJobId }

    override fun updateJob(delayedJob: DelayedJob) {
        jobRepository.updateJob(delayedJob)
    }

    override fun getJobs(filter: JobFilter) = filter.apply(jobRepository.getJobs())
}

val jobServiceProvider: JobService by lazy { JobServiceImpl(AlarmManagerServiceImpl(contextProvider()), jobRepositoryProvider) }