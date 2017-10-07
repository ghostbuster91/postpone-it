package io.github.ghostbuster91.postponeit.job.create

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
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
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class CreateJobActivity : RxAppCompatActivity() {

    private val jobService by lazy(jobServiceProvider)

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
        with(calendar) {
            setNewDate(day, month, year)
            dateInput.setOnClickListener {
                showDatePicker(year, month, day)
            }
        }
    }

    private fun initTimePicker(calendar: Calendar) {
        with(calendar) {
            setNewTime(calendar.toDate())
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
                    setNewTime(Calendar.getInstance().apply {
                        hour = h
                        minute = m
                    }.toDate())
                },
                currentHour,
                currentMinute,
                true)
        dpd.show(fragmentManager, "TimepickerDialog")
    }

    private fun setNewTime(time: Date) {
        val timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT, resources.configuration.locale)
        timeInput.setText(timeFormat.format(time))
    }

    private fun Int.leadingZero() = String.format("%02d", this)

    private fun scheduleSendingSms() {
        val isValidationOk = listOf(
                emptyValidator(smsNumberInput, "Number cannot be empty"),
                emptyValidator(smsTextInput, "Text cannot be empty"),
                emptyValidator(timeInput, "Time cannot be empty"),
                emptyValidator(dateInput, "Date cannot be empty"))
                .all { it }
        if (isValidationOk) {
            val timeInMillis = getTimeInMillis()
            jobService.createJob(timeInMillis, smsTextInput.text.toString(), smsNumberInput.text.toString())
            finish()
        }
    }

    private fun emptyValidator(input: TextInputEditText, errorMessage: String): Boolean {
        return if (input.text.isBlank()) {
            input.error = errorMessage
            false
        } else {
            input.error = null
            true
        }
    }

    private fun getTimeInMillis(): Long {
        return Calendar.getInstance().apply {
            val time = timeInput.text.split(":")
            val date = dateInput.text.split("/")
            hour = time.first().toInt()
            minute = time.last().toInt()
            day = date.first().toInt()
            month = date[1].toInt()
            year = date[2].toInt()
        }.timeInMillis
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, CreateJobActivity::class.java))
        }
    }
}