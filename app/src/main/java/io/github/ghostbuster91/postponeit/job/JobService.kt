package io.github.ghostbuster91.postponeit.job

import android.app.AlarmManager.RTC_WAKEUP
import io.github.ghostbuster91.postponeit.contextProvider
import io.github.ghostbuster91.postponeit.job.execute.SendSmsJobExecutor


interface JobService {
    fun createJob(timeInMillis: Long, smsText: String, smsTextNumber: String)
    fun cancelJob(delayedJob: DelayedJob)
    fun findJob(delayedJobId: Int): DelayedJob
    fun updateJob(delayedJob: DelayedJob)
    fun getJobs(): List<DelayedJob>
}

private class JobServiceImpl(
        private val context: android.content.Context,
        private val jobRepository: JobRepository) : JobService {
    private val alarmManager by lazy { context.getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager }

    override fun createJob(timeInMillis: Long, smsText: String, smsTextNumber: String) {
        val id = Math.abs(java.util.Random().nextInt())
        val delayedJob = DelayedJob(id = id, text = smsText, number = smsTextNumber, timeInMillis = timeInMillis)
        val pendingIntent = createAlarmIntent(delayedJob.id)
        alarmManager.setExact(RTC_WAKEUP, timeInMillis, pendingIntent)
        jobRepository.addJob(delayedJob)
    }

    override fun cancelJob(delayedJob: DelayedJob) {
        val sender = createAlarmIntent(delayedJob.id)
        val alarmManager = context.getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager
        alarmManager.cancel(sender)
        jobRepository.updateJob(delayedJob.copy(status = DelayedJobStatus.CANCELED))
    }

    override fun findJob(delayedJobId: Int): DelayedJob = jobRepository.getJobs().first { it.id == delayedJobId }

    override fun updateJob(delayedJob: DelayedJob) {
        jobRepository.updateJob(delayedJob)
    }

    override fun getJobs() = jobRepository.getJobs()

    private fun createAlarmIntent(delayedJobId: Int): android.app.PendingIntent? {
        val intent = SendSmsJobExecutor.Companion.intent(context, delayedJobId)
        return android.app.PendingIntent.getBroadcast(context, 0, intent, 0)
    }
}

val jobServiceProvider: JobService by lazy { JobServiceImpl(contextProvider(), jobRepositoryProvider) }