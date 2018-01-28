package io.github.ghostbuster91.postponeit.job.list

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.format.DateFormat.getDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithLayoutAndBinder
import com.elpassion.android.commons.recycler.basic.BasicAdapter
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import com.github.salomonbrys.kodein.LazyKodein
import com.github.salomonbrys.kodein.LazyKodeinAware
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.trello.rxlifecycle2.components.support.RxFragment
import io.github.ghostbuster91.postponeit.R
import io.github.ghostbuster91.postponeit.job.DelayedJob
import io.github.ghostbuster91.postponeit.job.DelayedJobStatus
import io.github.ghostbuster91.postponeit.job.JobFilter
import io.github.ghostbuster91.postponeit.job.JobService
import io.github.ghostbuster91.postponeit.job.create.CreateJobActivity
import io.github.ghostbuster91.postponeit.utils.SwipingItemTouchHelper
import io.github.ghostbuster91.postponeit.utils.toDate
import kotlinx.android.synthetic.main.job_layout.view.*
import kotlinx.android.synthetic.main.job_list.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class JobListFragment : RxFragment(), LazyKodeinAware {
    override val kodein: LazyKodein = LazyKodein(appKodein)
    private val jobService by instance<JobService>()

    private val filter by lazy { arguments!!.getSerializable(FILTER_KEY) as JobFilter }
    private val basicAdapter: BasicAdapter<DelayedJob> by lazy {
        basicAdapterWithLayoutAndBinder(jobService.getJobs(filter = filter), R.layout.job_layout, this::bindJob)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.job_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        jobList.layoutManager = LinearLayoutManager(context)
        val adapter = basicAdapter
        jobList.adapter = adapter
        createDelayedSmsButton.setOnClickListener {
            CreateJobActivity.start(context!!)
        }
        if (filter == JobFilter.PENDING) {
            val itemTouchHelper = ItemTouchHelper(SwipingItemTouchHelper(context!!, { position -> cancelJob(basicAdapter.items[position].id) }))
            itemTouchHelper.attachToRecyclerView(jobList)
        }
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        jobList.addItemDecoration(dividerItemDecoration)
    }

    override fun onResume() {
        super.onResume()
        basicAdapter.items = jobService.getJobs(filter = filter)
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
            jobStatus.text = jobStatsDisplayNameResolver(item)
        }
    }

    private fun jobStatsDisplayNameResolver(item: DelayedJob): CharSequence? {
        return when (item.status) {
            DelayedJobStatus.Pending -> getString(R.string.common_job_status_pending)
            DelayedJobStatus.Executed -> getString(R.string.common_job_status_executed)
            DelayedJobStatus.Canceled -> getString(R.string.common_job_status_canceled)
            is DelayedJobStatus.Error -> getString(R.string.common_job_status_error)
            DelayedJobStatus.Sent -> getString(R.string.common_job_status_sent)
            DelayedJobStatus.Delivered -> getString(R.string.common_job_status_delivered)
        }
    }

    private fun cancelJob(jobToCancelId: String) {
        jobService.cancelJob(jobToCancelId)
        basicAdapter.items = jobService.getJobs(filter = filter)
        jobList.adapter.notifyDataSetChanged()
    }

    companion object {
        private const val FILTER_KEY = "filter"
        fun newInstance(filter: JobFilter = JobFilter.PENDING): JobListFragment {
            return JobListFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(FILTER_KEY, filter)
                }
            }
        }
    }
}