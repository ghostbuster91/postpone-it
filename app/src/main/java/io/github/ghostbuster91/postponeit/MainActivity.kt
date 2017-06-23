package io.github.ghostbuster91.postponeit

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.Trigger
import com.jakewharton.rxbinding2.view.clicks
import com.tbruyelle.rxpermissions2.RxPermissions
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : RxAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val requestSmsPermission = RxPermissions(this).request(Manifest.permission.SEND_SMS)
        sendSmsButton.clicks()
                .bindToLifecycle(this)
                .flatMap { requestSmsPermission }
                .subscribe { granted ->
                    if (granted) {
                        onSmsClick()
                    } else {
                        Toast.makeText(this@MainActivity, "not granted", Toast.LENGTH_LONG).show()
                    }
                }
    }

    private fun onSmsClick() {
        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(this))

        val myJob = dispatcher.newJobBuilder()
                .setService(FirebaseJobService::class.java)
                .setTrigger(Trigger.executionWindow(0, 10))
                .setTag("my-unique-tag2")
                .setRecurring(false)
                .build()
        dispatcher.mustSchedule(myJob)
    }
}