package io.github.ghostbuster91.postponeit

import android.app.Notification
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.telephony.SmsManager
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

class FirebaseJobService : JobService() {

    private val jobRepository = jobRepositoryProvider

    override fun onStartJob(job: JobParameters): Boolean {
        Completable.fromCallable { sendSms() }
                .subscribeOn(Schedulers.computation())
                .doOnComplete { showNotification() }
                .subscribe({
                    jobRepository.updateJob(job.jobId, JobStatus.SUCCESS)
                    jobFinished(job, false)
                }, {
                    jobRepository.updateJob(job.jobId, JobStatus.SUCCESS)
                    jobFinished(job, false)
                })
        return true
    }

    private fun sendSms() {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage("23123123", null, "hello12", null, null)
    }

    private fun showNotification() {
        val n = Notification.Builder(this)
                .setContentTitle("New mail from " + "test@gmail.com")
                .setContentText("Subject")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .build()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, n)
    }

    override fun onStopJob(job: JobParameters): Boolean {
        return false
    }
}