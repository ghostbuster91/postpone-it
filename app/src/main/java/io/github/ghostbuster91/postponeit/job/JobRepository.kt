package io.github.ghostbuster91.postponeit.job

import android.content.Context
import android.preference.PreferenceManager
import com.elpassion.android.commons.sharedpreferences.CachingSharedPreferenceRepository
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs
import com.elpassion.sharedpreferences.gsonadapter.gsonConverterAdapter
import com.google.gson.Gson

interface JobRepository {

    fun getJobs(): List<DelayedJob>

    fun removeJob(delayedJobId: String)

    fun addJob(delayedJob: DelayedJob)

    fun updateJob(delayedJob: DelayedJob)
}

class JobRepositoryImpl(context: Context, gson: Gson) : JobRepository {
    private val sharedPrefs = CachingSharedPreferenceRepository(createSharedPrefs<List<DelayedJob>>({ PreferenceManager.getDefaultSharedPreferences(context) }, gsonConverterAdapter(gson)))

    override fun updateJob(delayedJob: DelayedJob) {
        val newJobList = getJobs().filter { it.id != delayedJob.id } + delayedJob
        sharedPrefs.write(KEY, newJobList)
    }

    override fun getJobs(): List<DelayedJob> = sharedPrefs.read(KEY) ?: emptyList()

    override fun removeJob(delayedJobId: String) {
        val newJobList = getJobs().filter { it.id != delayedJobId }
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