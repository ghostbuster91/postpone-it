package io.github.ghostbuster91.postponeit.job.list

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithLayoutAndBinder
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.github.ghostbuster91.postponeit.job.DelayedJob
import io.github.ghostbuster91.postponeit.R
import io.github.ghostbuster91.postponeit.job.create.CreateDelayedActivity
import io.github.ghostbuster91.postponeit.job.jobServiceProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.job_layout.view.*

class JobListActivity : RxAppCompatActivity() {

    private val jobService = jobServiceProvider
    private val items = mutableListOf<DelayedJob>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        jobList.layoutManager = LinearLayoutManager(this)
        jobList.adapter = basicAdapterWithLayoutAndBinder(items, R.layout.job_layout) { holder, item ->
            with(holder.itemView) {
                jobName.text = item.id.toString()
                targetSmsNumber.text = item.number
                jobDate.text = item.timeInMillis.toString()
                deleteJobButton.setOnClickListener {
                    onJobDelete(item)
                }
            }
        }
        createDelayedSmsButton.setOnClickListener {
            CreateDelayedActivity.start(this@JobListActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        items.clear()
        items.addAll(jobService.getJobs())
        jobList.adapter.notifyDataSetChanged()
    }

    private fun onJobDelete(item: DelayedJob) {
        jobService.deleteJob(item.id)
        items.remove(item)
        jobList.adapter.notifyDataSetChanged()
    }
}