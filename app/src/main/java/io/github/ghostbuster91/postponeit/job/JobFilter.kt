package io.github.ghostbuster91.postponeit.job

interface JobFilter {
    fun apply(input: List<DelayedJob>): List<DelayedJob>

    object ALL : JobFilter {
        override fun apply(input: List<DelayedJob>) = input
    }

    object PENDING : JobFilter {
        override fun apply(input: List<DelayedJob>) = input.filter { it.status == DelayedJobStatus.PENDING }
    }
}