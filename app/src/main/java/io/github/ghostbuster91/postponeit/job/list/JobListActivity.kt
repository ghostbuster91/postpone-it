package io.github.ghostbuster91.postponeit.job.list

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithLayoutAndBinder
import com.elpassion.android.commons.recycler.basic.BasicAdapter
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import com.elpassion.android.view.hide
import com.elpassion.android.view.show
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.github.ghostbuster91.postponeit.R
import io.github.ghostbuster91.postponeit.job.DelayedJob
import io.github.ghostbuster91.postponeit.job.DelayedJobStatus
import io.github.ghostbuster91.postponeit.job.create.CreateJobActivity
import io.github.ghostbuster91.postponeit.job.jobServiceProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.job_layout.view.*

class JobListActivity : RxAppCompatActivity() {

    private val jobService = jobServiceProvider
    private val basicAdapter: BasicAdapter<DelayedJob> by lazy {
        basicAdapterWithLayoutAndBinder(jobService.getJobs(), R.layout.job_layout, this::bindJob)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        jobList.layoutManager = LinearLayoutManager(this)
        val adapter = basicAdapter
        jobList.adapter = adapter
        createDelayedSmsButton.setOnClickListener {
            CreateJobActivity.start(this@JobListActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        basicAdapter.items = jobService.getJobs()
        jobList.adapter.notifyDataSetChanged()
    }

    private fun bindJob(holder: ViewHolderBinder<DelayedJob>, item: DelayedJob) {
        with(holder.itemView) {
            jobName.text = item.id.toString()
            jobStatus.text = item.status.toString()
            targetSmsNumber.text = item.number
            jobDate.text = item.timeInMillis.toString()
            if (item.status == DelayedJobStatus.PENDING) {
                cancelJobButton.show()
            } else {
                cancelJobButton.hide()
            }
            cancelJobButton.setOnClickListener {
                onJobCancel(item)
            }
        }
    }

    private fun onJobCancel(item: DelayedJob) {
        jobService.cancelJob(item)
        basicAdapter.items = jobService.getJobs()
        jobList.adapter.notifyDataSetChanged()
    }
}