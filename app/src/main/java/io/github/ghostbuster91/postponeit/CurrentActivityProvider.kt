package io.github.ghostbuster91.postponeit

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle

@SuppressLint("StaticFieldLeak")
object CurrentActivityProvider : Application.ActivityLifecycleCallbacks {

    var currentActivity : Activity? = null

    override fun onActivityPaused(activity: Activity?) {
        currentActivity = null
    }

    override fun onActivityResumed(activity: Activity?) {
        currentActivity = activity
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        currentActivity = activity
    }
}