package io.github.ghostbuster91.postponeit

import android.app.AlarmManager
import android.app.PendingIntent
import io.github.ghostbuster91.postponeit.job.DelayedJob
import io.github.ghostbuster91.postponeit.job.execute.SendSmsJobExecutor

val alarmManagerServiceProvider: () -> AlarmManagerService = { AlarmManagerServiceImpl(contextProvider()) }

interface AlarmManagerService {
    fun createAlarm(job: DelayedJob)

    fun cancelAlarm(jobId: String)
}

private class AlarmManagerServiceImpl(private val context: android.content.Context) : AlarmManagerService {

    private val alarmManager by lazy { context.getSystemService(android.content.Context.ALARM_SERVICE) as android.app.AlarmManager }

    override fun createAlarm(job: DelayedJob) {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, job.timeInMillis, createAlarmIntent(job.id))
    }

    override fun cancelAlarm(jobId: String) {
        alarmManager.cancel(createAlarmIntent(jobId))
    }

    private fun createAlarmIntent(delayedJobId: String): android.app.PendingIntent? {
        val intent = SendSmsJobExecutor.intent(context, delayedJobId)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}