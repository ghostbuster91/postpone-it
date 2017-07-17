package io.github.ghostbuster91.postponeit

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*


class JobCreator(private val context: Context,
                 private val jobRepository: JobRepository) {
    fun createJob(timeInMillis: Long, smsText: String, smsTextNumber: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(RTC_WAKEUP, timeInMillis, PendingIntent.getBroadcast(context, 0, Intent(context, SendSmsBroadcastReceiver::class.java), 0))
        val id = UUID.randomUUID().toString()
        jobRepository.addJob(DelayedJob(id = id, text = smsText, number = smsTextNumber, timeInMillis = timeInMillis))
    }
}

val jobServiceProvider by lazy { JobCreator(contextProvider(), jobRepositoryProvider) }