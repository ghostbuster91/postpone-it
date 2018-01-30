package io.github.ghostbuster91.postponeit.result

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.telephony.SmsManager
import com.github.salomonbrys.kodein.instance
import io.github.ghostbuster91.postponeit.R
import io.github.ghostbuster91.postponeit.job.DelayedJob
import io.github.ghostbuster91.postponeit.job.DelayedJobStatus
import io.github.ghostbuster91.postponeit.job.ErrorType
import io.github.ghostbuster91.postponeit.job.execute.NotificationService
import io.github.ghostbuster91.postponeit.job.execute.SendSmsJobExecutor
import io.github.ghostbuster91.postponeit.job.execute.wrapWithPendingIntent

class SmsSendingResultReceiver : SmsResultReceiver() {
    private val notificationService by instance<NotificationService>()

    override fun mapResultToJobStatus(resultCode: Int, delayedJob: DelayedJob, context: Context): DelayedJobStatus {
        val status = when (resultCode) {
            Activity.RESULT_OK -> DelayedJobStatus.Sent
            SmsManager.RESULT_ERROR_GENERIC_FAILURE -> DelayedJobStatus.Error(ErrorType.SENT_ERROR_GENERIC)
            SmsManager.RESULT_ERROR_NO_SERVICE -> DelayedJobStatus.Error(ErrorType.SENT_ERROR_NO_SERVICE)
            SmsManager.RESULT_ERROR_NULL_PDU -> DelayedJobStatus.Error(ErrorType.SENT_ERROR_NULL_PDU)
            SmsManager.RESULT_ERROR_RADIO_OFF -> DelayedJobStatus.Error(ErrorType.SENT_ERROR_RADIO_OFF)
            else -> throw NotImplementedError("Unexpected error code")
        }
        if (status is DelayedJobStatus.Error && status.errorType != ErrorType.DELIVERED_ERROR_CANCELED) {
            val resendIntent = SendSmsJobExecutor.intent(context, delayedJob.id)
            notificationService.showNotification(
                    "Sending message failed..",
                    status.errorType.toString(),
                    { addAction(createRetryAction(resendIntent, context)) })
        }
        return status
    }

    private fun createRetryAction(resendIntent: Intent, context: Context) =
            NotificationCompat.Action(R.drawable.ic_loop_black_24dp, "Retry", resendIntent.wrapWithPendingIntent(context))

    companion object {
        fun createIntent(context: Context, jobId: String) =
                Intent(context, SmsSendingResultReceiver::class.java)
                        .putExtra(JOB_ID, jobId)
    }
}