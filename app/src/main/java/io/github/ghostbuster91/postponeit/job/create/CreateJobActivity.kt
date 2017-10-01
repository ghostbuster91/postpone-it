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
import io.github.ghostbuster91.postponeit.utils.*
import kotlinx.android.synthetic.main.create_delayed_layout.*
import java.util.*

class CreateJobActivity : RxAppCompatActivity() {

    private val jobService = jobServiceProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_delayed_layout)
        setSupportActionBar(toolbar)
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
        val calendar = Calendar.getInstance()
        initTimePicker(calendar)
        initDatePicker(calendar)
    }

    private fun initDatePicker(calendar: Calendar) {
        with(calendar){
            setNewDate(day, month, year)
            dateInput.setOnClickListener {
                showDatePicker(year, month, day)
            }
        }
    }

    private fun initTimePicker(calendar: Calendar) {
        with(calendar){
            setNewTime(hour, minute)
            timeInput.setOnClickListener {
                showTimePicker(hour, minute)
            }
        }
    }

    private fun showDatePicker(currentYear: Int, currentMonth: Int, currentDay: Int) {
        val dpd = DatePickerDialog.newInstance(
                { _, y, m, d ->
                    setNewDate(d, m, y)
                },
                currentYear,
                currentMonth,
                currentDay)
        dpd.show(fragmentManager, "Datepickerdialog")
    }

    private fun setNewDate(d: Int, m: Int, y: Int) {
        dateInput.setText(getString(R.string.create_job_date_format, d.leadingZero(), m.leadingZero(), y.leadingZero()))
    }

    private fun showTimePicker(currentHour: Int, currentMinute: Int) {
        val dpd = TimePickerDialog.newInstance(
                { _, h, m, _ ->
                    setNewTime(h, m)
                },
                currentHour,
                currentMinute,
                true)
        dpd.show(fragmentManager, "TimepickerDialog")
    }

    private fun setNewTime(h: Int, m: Int) {
        timeInput.setText(getString(R.string.create_job_hour_format, h.leadingZero(), m.leadingZero()))
    }

    private fun Int.leadingZero() = String.format("%02d", this)

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