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
import io.github.ghostbuster91.postponeit.job.edit.EditJobActivity
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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.job_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        jobList.adapter?.notifyDataSetChanged()
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