package io.github.ghostbuster91.postponeit

import com.jakewharton.rxrelay2.PublishRelay
import io.github.ghostbuster91.postponeit.AppEvent.*
import io.github.ghostbuster91.postponeit.job.DelayedJob
import io.github.ghostbuster91.postponeit.job.DelayedJobStatus
import io.github.ghostbuster91.postponeit.job.JobService
import io.github.ghostbuster91.postponeit.job.execute.SendSmsJobExecutor
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

interface PostponeItModel {

    fun accept(event: AppEvent)
}

data class AppState(val i: String)

class PostponeItModelImpl(private val jobService: JobService,
                          private val smsJobExecutor: SendSmsJobExecutor,
                          private val requestJobAcceptanceService: RequestJobAcceptanceService) : PostponeItModel {

    private val events = PublishRelay.create<AppEvent>()

    private val appEvents = events
            .subscribeOn(Schedulers.single())

    private val newJobs = appEvents.ofType(AlarmFired::class.java)
            .transformToJob()
    private val jobWithPermissionGranted = appEvents.ofType(SendingJobAccepted::class.java)
            .transformToJob()
    private val canceledJobs = appEvents.ofType(JobCanceled::class.java)
            .transformToJob()
    private val deliveryFails = appEvents.ofType(JobDelivered::class.java)
            .transformToJob()
    private val sendingJobFails = appEvents.ofType(JobSend::class.java)
            .transformToJob()

    private val jobsWithAcceptance = newJobs.filter { it.requiresAcceptance }
    private val jobsWithoutAcceptance = newJobs.filter { !it.requiresAcceptance }

    init {
        executeJobs()
        cancelJobs()
        requestForAcceptance()
        deliveryFails
    }

    fun Observable<out AppEvent>.transformToJob(): Observable<DelayedJob> =
            map(AppEvent::delayedJobId)
                    .map(jobService::findJob)

    private fun requestForAcceptance() {
        jobsWithAcceptance
                .subscribe(requestJobAcceptanceService::requestAcceptance)
    }

    private fun executeJobs() {
        Observable.merge(jobsWithoutAcceptance, jobWithPermissionGranted)
                .subscribe(smsJobExecutor::executeDelayedJob)
    }

    private fun cancelJobs() {
        canceledJobs.subscribe { jobService.updateJob(it.copy(status = DelayedJobStatus.Canceled)) }
    }

    override fun accept(event: AppEvent) {
        events.accept(event)
    }
}

sealed class AppEvent {
    abstract val delayedJobId: String

    data class AlarmFired(override val delayedJobId: String) : AppEvent()
    data class SendingJobAccepted(override val delayedJobId: String) : AppEvent()
    data class JobCanceled(override val delayedJobId: String) : AppEvent()
    data class JobSend(override val delayedJobId: String, val deliveryStatus: DelayedJobStatus) : AppEvent()
    data class JobDelivered(override val delayedJobId: String) : AppEvent()
}

interface RequestJobAcceptanceService {
    fun requestAcceptance(delayedJob: DelayedJob)
}