package io.github.ghostbuster91.postponeit.job

import io.github.ghostbuster91.postponeit.job.create.Contact

data class DelayedJob(val id: String,
                      val text: String,
                      val contact: Contact,
                      val timeInMillis: Long,
                      val status: DelayedJobStatus = DelayedJobStatus.Pending,
                      val requiresAcceptance: Boolean)

sealed class DelayedJobStatus {
    object Pending : DelayedJobStatus()
    object Executed : DelayedJobStatus()
    object Canceled : DelayedJobStatus()
    data class Error(val errorType : ErrorType) : DelayedJobStatus()
    object Sent : DelayedJobStatus()
    object Delivered : DelayedJobStatus()
}

enum class ErrorType {
    DELIVERED_ERROR_CANCELED,
    SENT_ERROR_GENERIC,
    SENT_ERROR_NULL_PDU,
    SENT_ERROR_NO_SERVICE,
    SENT_ERROR_RADIO_OFF
}
