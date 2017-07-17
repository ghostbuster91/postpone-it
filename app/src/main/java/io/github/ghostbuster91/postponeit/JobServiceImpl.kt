package io.github.ghostbuster91.postponeit

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.content.Context
import java.util.*


interface JobService {
    fun createJob(timeInMillis: Long, smsText: String, smsTextNumber: String)
    fun deleteJob(delayedJobId: Int)
}

class JobServiceImpl(
        private val context: Context,
        private val jobRepository: JobRepository) : JobService {
    private val alarmManager by lazy { context.getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    override fun createJob(timeInMillis: Long, smsText: String, smsTextNumber: String) {
        val id = Math.abs(Random().nextInt())
        val delayedJob = DelayedJob(id = id, text = smsText, number = smsTextNumber, timeInMillis = timeInMillis)
        val pendingIntent = createAlarmIntent(delayedJob)
        alarmManager.setExact(RTC_WAKEUP, timeInMillis, pendingIntent)
        jobRepository.addJob(delayedJob)
    }

    override fun deleteJob(delayedJobId: Int) {
        val sender = createAlarmIntent(jobRepository.getJobs().first { it.id == delayedJobId })
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
        jobRepository.removeJob(delayedJobId)
    }

    private fun createAlarmIntent(delayedJob: DelayedJob): PendingIntent? {
        val intent = SendSmsBroadcastReceiver.intent(context, delayedJob)
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }
}

val jobServiceProvider: JobService by lazy { JobServiceImpl(contextProvider(), jobRepositoryProvider) }