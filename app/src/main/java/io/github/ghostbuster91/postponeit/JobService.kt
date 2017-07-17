package io.github.ghostbuster91.postponeit

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*


class JobService(private val context: Context,
                 private val jobRepository: JobRepository) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


    fun createJob(timeInMillis: Long, smsText: String, smsTextNumber: String) {
        val id = Math.abs(Random().nextInt())
        val delayedJob = DelayedJob(id = id, text = smsText, number = smsTextNumber, timeInMillis = timeInMillis)
        alarmManager.setExact(RTC_WAKEUP, timeInMillis, PendingIntent.getBroadcast(context, delayedJob.id, SendSmsBroadcastReceiver.intent(context, delayedJob), 0))
        jobRepository.addJob(delayedJob)
    }

    fun deleteJob(delayedJobId: Int) {
        jobRepository.removeJob(delayedJobId)
        val intent = Intent(context, SendSmsBroadcastReceiver::class.java)
        val sender = PendingIntent.getBroadcast(context, delayedJobId, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
    }
}

val jobServiceProvider by lazy { JobService(contextProvider(), jobRepositoryProvider) }