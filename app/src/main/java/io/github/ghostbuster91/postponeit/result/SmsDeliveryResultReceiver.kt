package io.github.ghostbuster91.postponeit.result

import android.app.Activity
import android.content.Context
import android.content.Intent
import io.github.ghostbuster91.postponeit.job.DelayedJobStatus

class SmsDeliveryResultReceiver : SmsResultReceiver() {

    override fun mapResultToJobStatus(resultCode: Int): DelayedJobStatus {
        return when (resultCode) {
            Activity.RESULT_OK -> DelayedJobStatus.DELIVERED_OK
            Activity.RESULT_CANCELED -> DelayedJobStatus.DELIVERED_ERROR_CANCELED
            else -> throw NotImplementedError("Unexpected error code")
        }
    }

    companion object {
        fun createIntent(context: Context, jobId: String) =
                Intent(context, SmsDeliveryResultReceiver::class.java)
                        .putExtra(JOB_ID, jobId)
    }
}