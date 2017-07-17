package io.github.ghostbuster91.postponeit.job.list

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithLayoutAndBinder
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.github.ghostbuster91.postponeit.R
import io.github.ghostbuster91.postponeit.job.DelayedJob
import io.github.ghostbuster91.postponeit.job.create.CreateJobActivity
import io.github.ghostbuster91.postponeit.job.jobServiceProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.job_layout.view.*

class JobListActivity : RxAppCompatActivity() {

    private val jobService = jobServiceProvider
    private var items = emptyList<DelayedJob>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        jobList.layoutManager = LinearLayoutManager(this)
        jobList.adapter = basicAdapterWithLayoutAndBinder(items, R.layout.job_layout) { holder, item ->
            with(holder.itemView) {
                jobName.text = item.id.toString()
                jobStatus.text = item.status.toString()
                targetSmsNumber.text = item.number
                jobDate.text = item.timeInMillis.toString()
                cancelJobButton.setOnClickListener {
                    onJobCancel(item)
                }
            }
        }
        createDelayedSmsButton.setOnClickListener {
            CreateJobActivity.start(this@JobListActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        items = jobService.getJobs()
        jobList.adapter.notifyDataSetChanged()
    }

    private fun onJobCancel(item: DelayedJob) {
        jobService.cancelJob(item)
        items = jobService.getJobs()
        jobList.adapter.notifyDataSetChanged()
    }
}