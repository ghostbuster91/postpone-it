package io.github.ghostbuster91.postponeit.job

data class DelayedJob(val id: String,
                      val text: String,
                      val number: String,
                      val timeInMillis: Long,
                      val status: DelayedJobStatus = DelayedJobStatus.PENDING)

enum class DelayedJobStatus {
    PENDING,
    EXECUTED,
    CANCELED,
    DELIVERED_OK,
    DELIVERED_ERROR_CANCELED,
    SENT_OK,
    SENT_ERROR_GENERIC,
    SENT_ERROR_NULL_PDU,
    SENT_ERROR_NO_SERVICE,
    SENT_ERROR_RADIO_OFF
}