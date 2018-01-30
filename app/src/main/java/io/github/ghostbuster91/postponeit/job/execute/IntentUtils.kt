package io.github.ghostbuster91.postponeit.job.execute

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

fun Intent.wrapWithPendingIntent(context: Context) =
        PendingIntent.getBroadcast(context, 0, this, PendingIntent.FLAG_UPDATE_CURRENT)