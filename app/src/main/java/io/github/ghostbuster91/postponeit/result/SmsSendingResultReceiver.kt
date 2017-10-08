package io.github.ghostbuster91.postponeit.result

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import io.github.ghostbuster91.postponeit.job.DelayedJobStatus
import io.github.ghostbuster91.postponeit.job.ErrorType

class SmsSendingResultReceiver : SmsResultReceiver() {

    override fun mapResultToJobStatus(resultCode: Int): DelayedJobStatus {
        return when (resultCode) {
            Activity.RESULT_OK -> DelayedJobStatus.Sent
            SmsManager.RESULT_ERROR_GENERIC_FAILURE -> DelayedJobStatus.Error(ErrorType.SENT_ERROR_GENERIC)
            SmsManager.RESULT_ERROR_NO_SERVICE -> DelayedJobStatus.Error(ErrorType.SENT_ERROR_NO_SERVICE)
            SmsManager.RESULT_ERROR_NULL_PDU -> DelayedJobStatus.Error(ErrorType.SENT_ERROR_NULL_PDU)
            SmsManager.RESULT_ERROR_RADIO_OFF -> DelayedJobStatus.Error(ErrorType.SENT_ERROR_RADIO_OFF)
            else -> throw NotImplementedError("Unexpected error code")
        }
    }

    companion object {
        fun createIntent(context: Context, jobId: String) =
                Intent(context, SmsSendingResultReceiver::class.java)
                        .putExtra(JOB_ID, jobId)
    }
}