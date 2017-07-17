package io.github.ghostbuster91.postponeit

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithLayoutAndBinder
import com.elpassion.android.commons.recycler.basic.addAll
import com.elpassion.android.commons.recycler.basic.asBasicMutableList
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.job_layout.view.*

class MainActivity : RxAppCompatActivity() {

    private val jobRepository = jobRepositoryProvider
    private val jobService = jobServiceProvider
    private val items = mutableListOf<DelayedJob>().asBasicMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        jobList.layoutManager = LinearLayoutManager(this)
        jobList.adapter = basicAdapterWithLayoutAndBinder(items, R.layout.job_layout) { holder, item ->
            holder.itemView.jobName.text = item.id.toString()
            holder.itemView.targetSmsNumber.text = item.number
            holder.itemView.jobDate.text = item.timeInMillis.toString()
            holder.itemView.deleteJobButton.setOnClickListener {
                jobService.deleteJob(item.id)
            }
        }
        createDelayedSmsButton.setOnClickListener {
            CreateDelayedActivity.start(this@MainActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        items.clear()
        items.addAll(jobRepository.getJobs())
        jobList.adapter.notifyDataSetChanged()
    }
}