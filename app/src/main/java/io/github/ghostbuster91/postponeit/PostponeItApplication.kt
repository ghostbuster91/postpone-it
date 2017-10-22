package io.github.ghostbuster91.postponeit

import android.app.Application
import io.github.ghostbuster91.postponeit.job.JobRepositoryImpl
import io.github.ghostbuster91.postponeit.job.JobServiceImpl
import io.github.ghostbuster91.postponeit.job.create.CreateJobActivity
import io.github.ghostbuster91.postponeit.job.execute.SendSmsJobExecutor
import io.github.ghostbuster91.postponeit.job.gsonProvider
import io.github.ghostbuster91.postponeit.job.list.JobListFragment
import io.github.ghostbuster91.postponeit.result.SmsResultReceiver

class PostponeItApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val alarmManager = AlarmManagerServiceImpl(applicationContext)
        val jobRepository = JobRepositoryImpl(applicationContext, gsonProvider())
        val jobService = JobServiceImpl(alarmManager, jobRepository)

        RestoreAlarmsOnBootCompletedReceiver.alarmServiceManager = alarmManager
        RestoreAlarmsOnBootCompletedReceiver.jobRepository = jobRepository
        SmsResultReceiver.jobService = jobService
        SendSmsJobExecutor.jobService = jobService

        CreateJobActivity.jobService = jobService
        JobListFragment.jobService = jobService
    }
}