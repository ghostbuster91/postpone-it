package io.github.ghostbuster91.postponeit

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context

val jobServiceProvider by lazy { JobServiceImpl(contextProvider(), jobRepositoryProvider) }

interface JobService {
    fun createJob()
}

class JobServiceImpl(private val context: Context,
                     private val jobRepository: JobRepository) : JobService {

    override fun createJob() {
        val jobId = jobRepository.getJobs().map { it.id }.max()?.plus(1) ?: 0
        val builder = createJob(jobId)
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(builder)
        jobRepository.addJob(DelayedJob(jobId, JobStatus.PENDING))
    }

    private fun createJob(jobId: Int): JobInfo {
        return JobInfo.Builder(jobId, ComponentName(context, FirebaseJobService::class.java))
                .setMinimumLatency((1 * 1000).toLong()) // wait at least
                .setOverrideDeadline((5 * 1000).toLong()) // maximum delay
                .build()
    }
}