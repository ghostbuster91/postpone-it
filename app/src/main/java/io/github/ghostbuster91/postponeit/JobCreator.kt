package io.github.ghostbuster91.postponeit

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.content.Context
import android.content.Intent


class JobCreator(private val context: Context) {
    fun createJob(timeInMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(RTC_WAKEUP, timeInMillis, PendingIntent.getBroadcast(context, 0, Intent(context, SendSmsBroadcastReceiver::class.java), 0))
    }
}

val jobServiceProvider by lazy { JobCreator(contextProvider()) }