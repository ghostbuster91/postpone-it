package io.github.ghostbuster91.postponeit.job

data class DelayedJob(val id: String,
                      val text: String,
                      val number: String,
                      val timeInMillis: Long,
                      val status: DelayedJobStatus = DelayedJobStatus.PENDING)

enum class DelayedJobStatus {
    PENDING,
    EXECUTED,
    CANCELED
}