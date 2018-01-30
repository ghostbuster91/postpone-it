package io.github.ghostbuster91.postponeit.result

import android.app.Activity
import android.content.Context
import android.content.Intent
import io.github.ghostbuster91.postponeit.job.DelayedJob
import io.github.ghostbuster91.postponeit.job.DelayedJobStatus
import io.github.ghostbuster91.postponeit.job.ErrorType

class SmsDeliveryResultReceiver : SmsResultReceiver() {

    override fun mapResultToJobStatus(resultCode: Int, delayedJob: DelayedJob, context: Context): DelayedJobStatus {
        return when (resultCode) {
            Activity.RESULT_OK -> DelayedJobStatus.Delivered
            Activity.RESULT_CANCELED -> DelayedJobStatus.Error(errorType = ErrorType.DELIVERED_ERROR_CANCELED)
            else -> throw NotImplementedError("Unexpected error code")
        }
    }

    companion object {
        fun createIntent(context: Context, jobId: String) =
                Intent(context, SmsDeliveryResultReceiver::class.java)
                        .putExtra(JOB_ID, jobId)
    }
}