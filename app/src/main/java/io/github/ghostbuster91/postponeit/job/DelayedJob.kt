package io.github.ghostbuster91.postponeit.job

data class DelayedJob(val id: Int,
                      val text: String,
                      val number: String,
                      val timeInMillis: Long,
                      val status: DelayedJobStatus = DelayedJobStatus.PENDING)

enum class DelayedJobStatus {
    PENDING,
    EXECUTED
}