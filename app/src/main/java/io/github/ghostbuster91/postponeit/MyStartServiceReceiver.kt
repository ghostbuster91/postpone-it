package io.github.ghostbuster91.postponeit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyStartServiceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        Log.e(MyStartServiceReceiver::class.java.name, "OnBootCompleted")
    }
}