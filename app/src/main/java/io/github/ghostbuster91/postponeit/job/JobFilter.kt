package io.github.ghostbuster91.postponeit.job

enum class JobFilter {
    ALL {
        override fun apply(input: List<DelayedJob>) = input
    },
    PENDING {
        override fun apply(input: List<DelayedJob>) = input.filter { it.status == DelayedJobStatus.PENDING }

    };

    abstract fun apply(input: List<DelayedJob>): List<DelayedJob>
}