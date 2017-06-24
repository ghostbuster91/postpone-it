package io.github.ghostbuster91.postponeit

import android.Manifest
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
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
        val serviceComponent = ComponentName(this, FirebaseJobService::class.java)
        val builder = JobInfo.Builder(0, serviceComponent)
                .setMinimumLatency((1 * 1000).toLong()) // wait at least
                .setOverrideDeadline((5 * 1000).toLong()) // maximum delay
        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(builder.build())
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, CreateDelayedActivity::class.java))
        }
    }
}