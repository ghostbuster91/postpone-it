package io.github.ghostbuster91.postponeit

import android.content.Context
import android.preference.PreferenceManager
import com.elpassion.android.commons.sharedpreferences.CachingSharedPreferenceRepository
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs
import com.google.gson.Gson

val jobRepositoryProvider by lazy { JobRepositoryImpl(contextProvider()) }

interface JobRepository {

    fun getJobs(): List<DelayedJob>

    fun removeJob(delayedJob: DelayedJob)

    fun addJob(delayedJob: DelayedJob)

    fun updateJob(jobId: Int, status: JobStatus)
}

class JobRepositoryImpl(private val context: Context) : JobRepository {
    private val sharedPrefs = CachingSharedPreferenceRepository(createSharedPrefs<List<DelayedJob>>({ PreferenceManager.getDefaultSharedPreferences(context) }, { Gson() }))

    override fun updateJob(jobId: Int, status: JobStatus) {
        val newJobList = getJobs().filter { it.id != jobId } + DelayedJob(jobId, status)
        sharedPrefs.write(KEY, newJobList)
    }

    override fun getJobs(): List<DelayedJob> = sharedPrefs.read(KEY) ?: emptyList()

    override fun removeJob(delayedJob: DelayedJob) {
        val newJobList = getJobs().filter { it != delayedJob }
        sharedPrefs.write(KEY, newJobList)
    }

    override fun addJob(delayedJob: DelayedJob) {
        val newJobList = getJobs() + delayedJob
        sharedPrefs.write(KEY, newJobList)
    }

    companion object {
        private const val KEY = "DELAYED_JOBS"
    }
}

data class DelayedJob(val id: Int, val status: JobStatus)

enum class JobStatus {
    PENDING,
    SUCCESS,
    FAILURE
}
