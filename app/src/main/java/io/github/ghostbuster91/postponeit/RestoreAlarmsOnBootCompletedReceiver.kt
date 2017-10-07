package io.github.ghostbuster91.postponeit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.github.ghostbuster91.postponeit.job.jobRepositoryProvider

class RestoreAlarmsOnBootCompletedReceiver : BroadcastReceiver() {

    private val alarmServiceManager by lazy(alarmManagerServiceProvider)
    private val jobRepository by lazy(jobRepositoryProvider)

    override fun onReceive(context: Context?, intent: Intent) {
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