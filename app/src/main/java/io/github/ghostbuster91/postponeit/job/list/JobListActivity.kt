package io.github.ghostbuster91.postponeit.job.list

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithLayoutAndBinder
import com.elpassion.android.commons.recycler.basic.BasicAdapter
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.github.ghostbuster91.postponeit.R
import io.github.ghostbuster91.postponeit.job.DelayedJob
import io.github.ghostbuster91.postponeit.job.create.CreateJobActivity
import io.github.ghostbuster91.postponeit.job.jobServiceProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.job_layout.view.*

class JobListActivity : RxAppCompatActivity() {

    private val jobService = jobServiceProvider
    private val basicAdapter: BasicAdapter<DelayedJob> by lazy {
        basicAdapterWithLayoutAndBinder(jobService.getJobs(), R.layout.job_layout) { holder, item ->
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

    private fun onJobCancel(item: DelayedJob) {
        jobService.cancelJob(item)
        basicAdapter.items = jobService.getJobs()
        jobList.adapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        basicAdapter.items = jobService.getJobs()
        jobList.adapter.notifyDataSetChanged()
        super.onActivityResult(requestCode, resultCode, data)
    }
}