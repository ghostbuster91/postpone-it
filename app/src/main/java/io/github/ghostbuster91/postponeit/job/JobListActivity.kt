package io.github.ghostbuster91.postponeit.job

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.format.DateFormat
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithLayoutAndBinder
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import com.github.salomonbrys.kodein.LazyKodein
import com.github.salomonbrys.kodein.LazyKodeinAware
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxrelay2.PublishRelay
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.github.ghostbuster91.postponeit.R
import io.github.ghostbuster91.postponeit.job.edit.EditJobActivity
import io.github.ghostbuster91.postponeit.utils.SwipingItemTouchHelper
import io.github.ghostbuster91.postponeit.utils.toDate
import io.reactivex.Observable
import kotlinx.android.synthetic.main.job_layout.view.*
import kotlinx.android.synthetic.main.job_list.*
import java.text.SimpleDateFormat
import java.util.*

class JobListActivity : RxAppCompatActivity(), LazyKodeinAware, ReactView<List<DelayedJob>, AppEvent> {
    override val kodein: LazyKodein = LazyKodein(appKodein)
    private val appModel by instance<AppModel>()
    private val eventS = PublishRelay.create<AppEvent>()
    override val events: Observable<AppEvent> by lazy { eventS.mergeWith(createDelayedSmsButton.clicks().map { AppEvent.CreateJobClicked }) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.job_list)
        createView()
        bind({ appModel.events to appModel.jobList }, this)
    }

    private fun createView() {
        jobList.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        jobList.addItemDecoration(dividerItemDecoration)
    }

    @SuppressLint("CheckResult")
    override fun render(jobsS: Observable<List<DelayedJob>>) {
        jobsS.subscribe {jobs->
            val adapter = basicAdapterWithLayoutAndBinder(jobs, R.layout.job_layout, this::bindJob)
            jobList.adapter = adapter
            val itemTouchHelper = ItemTouchHelper(SwipingItemTouchHelper(this) { position ->
                eventS.accept(AppEvent.JobCanceled(adapter.items[position].id))
            })
            itemTouchHelper.attachToRecyclerView(jobList)
        }
    }

    private fun bindJob(holder: ViewHolderBinder<DelayedJob>, item: DelayedJob) {
        with(holder.itemView) {
            targetSmsNumber.text = getString(R.string.job_list_send_to, item.contact.label)
            jobContent.text = getString(R.string.job_list_message, item.text)
            val calendar = Calendar.getInstance().apply { timeInMillis = item.timeInMillis }
            val dateFormat = DateFormat.getDateFormat(context)
            val timeFormat = SimpleDateFormat.getTimeInstance(java.text.DateFormat.SHORT, resources.configuration.locale)
            val toDate = calendar.toDate()
            jobDate.text = getString(R.string.job_list_date_time, dateFormat.format(toDate), timeFormat.format(toDate))
            jobStatus.text = jobStatsDisplayNameResolver(item)
            setOnClickListener {
                EditJobActivity.start(context, item.id)
            }
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

    companion object {
        fun intent(context: Context) = Intent(context, JobListActivity::class.java)
    }
}