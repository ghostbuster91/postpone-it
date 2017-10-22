package io.github.ghostbuster91.postponeit

import android.content.Context
import android.content.Intent
import android.util.Log
import com.github.salomonbrys.kodein.android.KodeinBroadcastReceiver
import com.github.salomonbrys.kodein.instance
import io.github.ghostbuster91.postponeit.job.JobRepository

class RestoreAlarmsOnBootCompletedReceiver : KodeinBroadcastReceiver() {
    private val jobRepository by instance<JobRepository>()
    private val alarmServiceManager by instance<AlarmManagerService>()

    override fun onBroadcastReceived(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val currentTime = System.currentTimeMillis()
        jobRepository.getJobs()
                .filter {
                    it.timeInMillis > currentTime
                }
                .forEach {
                    alarmServiceManager.createAlarm(it)
                }
        Log.d(RestoreAlarmsOnBootCompletedReceiver::class.java.name, "Alarms restored")
    }
}