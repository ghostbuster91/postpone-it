package io.github.ghostbuster91.postponeit.job.edit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.LinearLayout
import android.widget.TextView
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithLayoutAndBinder
import com.elpassion.android.view.disable
import com.github.salomonbrys.kodein.LazyKodein
import com.github.salomonbrys.kodein.LazyKodeinAware
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import io.github.ghostbuster91.postponeit.R
import io.github.ghostbuster91.postponeit.job.JobService
import io.github.ghostbuster91.postponeit.job.common.setTimeText
import io.github.ghostbuster91.postponeit.utils.day
import io.github.ghostbuster91.postponeit.utils.month
import io.github.ghostbuster91.postponeit.utils.toDate
import io.github.ghostbuster91.postponeit.utils.year
import kotlinx.android.synthetic.main.create_job_layout.*
import java.util.*

class EditJobActivity : AppCompatActivity(), LazyKodeinAware {
    override val kodein: LazyKodein = LazyKodein(appKodein)
    private val jobService by instance<JobService>()
    private val delayedJob by lazy { jobService.findJob(intent.getStringExtra(KEY)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_job_layout)
        smsTextInput.setText(delayedJob.text)
        smsTextInput.disable()
        val calendar = Calendar.getInstance().apply { timeInMillis = delayedJob.timeInMillis }
//        dateInput.setDateText(calendar.day, calendar.month, calendar.year)
        timeInput.setTimeText(calendar.toDate())
        contactSelector.disable()
        dateInput.disable()
        timeInput.disable()
        scheduleButton.disable()
        requiresAcceptanceView.disable()
        requiresAcceptanceView.isChecked = delayedJob.requiresAcceptance
        selectedContactsView.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        selectedContactsView.adapter = basicAdapterWithLayoutAndBinder(
                listOf(delayedJob.contact),
                R.layout.create_job_selected_contact_item,
                { holder, item ->
                    (holder.itemView as TextView).text = item.label
                })
    }

    companion object {
        private const val KEY = "key"
        fun start(context: Context, delayedJobId: String) {
            context.startActivity(Intent(context, EditJobActivity::class.java)
                    .putExtra(KEY, delayedJobId))
        }
    }
}