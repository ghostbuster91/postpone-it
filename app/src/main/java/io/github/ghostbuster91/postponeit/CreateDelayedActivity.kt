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
import kotlinx.android.synthetic.main.create_delayed_layout.*

class CreateDelayedActivity : RxActivity() {

    private val jobService = jobServiceProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_delayed_layout)
        val requestSmsPermission = RxPermissions(this).request(Manifest.permission.SEND_SMS)
        sendSmsButton.clicks()
                .bindToLifecycle(this)
                .flatMap { requestSmsPermission }
                .subscribe { granted ->
                    if (granted) {
                        onSmsClick()
                    } else {
                        Toast.makeText(this@CreateDelayedActivity, "not granted", Toast.LENGTH_LONG).show()
                    }
                }
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