package io.github.ghostbuster91.postponeit.job.list

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.format.DateFormat.getDateFormat
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithLayoutAndBinder
import com.elpassion.android.commons.recycler.basic.BasicAdapter
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.github.ghostbuster91.postponeit.R
import io.github.ghostbuster91.postponeit.job.DelayedJob
import io.github.ghostbuster91.postponeit.job.JobFilter
import io.github.ghostbuster91.postponeit.job.create.CreateJobActivity
import io.github.ghostbuster91.postponeit.job.jobServiceProvider
import io.github.ghostbuster91.postponeit.utils.SwipingItemTouchHelper
import io.github.ghostbuster91.postponeit.utils.toDate
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.job_layout.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class JobListActivity : RxAppCompatActivity() {

    private val jobService = jobServiceProvider
    private val basicAdapter: BasicAdapter<DelayedJob> by lazy {
        basicAdapterWithLayoutAndBinder(jobService.getJobs(filter = JobFilter.PENDING), R.layout.job_layout, this::bindJob)
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
        val itemTouchHelper = ItemTouchHelper(SwipingItemTouchHelper(this, { position -> cancelJob(basicAdapter.items[position].id) }))
        itemTouchHelper.attachToRecyclerView(jobList)
        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        jobList.addItemDecoration(dividerItemDecoration)
    }

    override fun onResume() {
        super.onResume()
        basicAdapter.items = jobService.getJobs(filter = JobFilter.PENDING)
        jobList.adapter.notifyDataSetChanged()
    }

    private fun bindJob(holder: ViewHolderBinder<DelayedJob>, item: DelayedJob) {
        with(holder.itemView) {
            targetSmsNumber.text = getString(R.string.job_list_send_to, item.number)
            jobContent.text = getString(R.string.job_list_message, item.text)
            val calendar = Calendar.getInstance().apply { timeInMillis = item.timeInMillis }
            val dateFormat = getDateFormat(context)
            val timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT, resources.configuration.locale)
            val toDate = calendar.toDate()
            jobDate.text = getString(R.string.job_list_date_time, dateFormat.format(toDate), timeFormat.format(toDate))
        }
    }

    private fun cancelJob(jobToCancelId: Int) {
        jobService.cancelJob(jobToCancelId)
        basicAdapter.items = jobService.getJobs(filter = JobFilter.PENDING)
        jobList.adapter.notifyDataSetChanged()
    }
}