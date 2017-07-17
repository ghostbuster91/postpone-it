package io.github.ghostbuster91.postponeit

data class DelayedJob(val id: Int,
                      val text: String,
                      val number: String,
                      val timeInMillis: Long,
                      val status: DelayedJobStatus = DelayedJobStatus.PENDING)

enum class DelayedJobStatus {
    PENDING,
    EXECUTED
}