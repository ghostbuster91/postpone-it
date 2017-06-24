package io.github.ghostbuster91.postponeit

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.jakewharton.rxbinding2.view.clicks
import com.tbruyelle.rxpermissions2.RxPermissions
import com.trello.rxlifecycle2.components.RxActivity
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.create_delayed_layout.*
import java.util.*

class CreateDelayedActivity : RxActivity() {

    private val jobService = jobServiceProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_delayed_layout)
        val requestSmsPermission = RxPermissions(this).request(Manifest.permission.SEND_SMS)
        scheduleButton.clicks()
                .bindToLifecycle(this)
                .flatMap { requestSmsPermission }
                .subscribe { granted ->
                    if (granted) {
                        onSmsClick()
                    } else {
                        Toast.makeText(this@CreateDelayedActivity, "not granted", Toast.LENGTH_LONG).show()
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

    private fun onSmsClick() {
        jobService.createJob()
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, CreateDelayedActivity::class.java))
        }
    }
}