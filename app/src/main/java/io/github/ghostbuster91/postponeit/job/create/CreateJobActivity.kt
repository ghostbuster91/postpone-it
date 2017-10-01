package io.github.ghostbuster91.postponeit.job.create

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.jakewharton.rxbinding2.view.clicks
import com.tbruyelle.rxpermissions2.RxPermissions
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import io.github.ghostbuster91.postponeit.R
import io.github.ghostbuster91.postponeit.job.jobServiceProvider
import kotlinx.android.synthetic.main.create_delayed_layout.*
import java.util.*

class CreateJobActivity : RxAppCompatActivity() {

    private val jobService = jobServiceProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_delayed_layout)
        val requestSmsPermission = RxPermissions(this).request(Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE)
        scheduleButton.clicks()
                .bindToLifecycle(this)
                .flatMap { requestSmsPermission }
                .subscribe { granted ->
                    if (granted) {
                        scheduleSendingSms()
                    } else {
                        Toast.makeText(this@CreateJobActivity, "not granted", Toast.LENGTH_LONG).show()
                    }
                }
        timeInput.setOnClickListener {
            showTimePicker()
        }
        dateInput.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val now = Calendar.getInstance()
        val dpd = DatePickerDialog.newInstance(
                { v, y, m, d ->
                    dateInput.setText("$d/$m/$y")
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH))
        dpd.show(fragmentManager, "Datepickerdialog")
    }

    private fun showTimePicker() {
        val now = Calendar.getInstance()
        val dpd = TimePickerDialog.newInstance(
                { v, h, m, s ->
                    timeInput.setText("$h:$m")
                },
                now.get(Calendar.HOUR),
                now.get(Calendar.MINUTE),
                true)
        dpd.show(fragmentManager, "Datepickerdialog")
    }

    private fun scheduleSendingSms() {
        val timeInMillis = getTimeInMillis()
        jobService.createJob(timeInMillis, smsTextInput.text.toString(), smsNumberInput.text.toString())
        finish()
    }

    private fun getTimeInMillis(): Long {
        return Calendar.getInstance().apply {
            val time = timeInput.text.split(":")
            val date = dateInput.text.split("/")
            set(Calendar.HOUR, time.first().toInt())
            set(Calendar.MINUTE, time.last().toInt())
            set(Calendar.DAY_OF_MONTH, date.first().toInt())
            set(Calendar.MONTH, date[1].toInt())
            set(Calendar.YEAR, date[2].toInt())
        }.timeInMillis
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, CreateJobActivity::class.java))
        }
    }
}