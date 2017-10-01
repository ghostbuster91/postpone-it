package io.github.ghostbuster91.postponeit.utils

import java.util.*

val Calendar.day: Int
    get() = get(Calendar.DAY_OF_MONTH)

val Calendar.month: Int
    get() = get(Calendar.MONTH)

val Calendar.year: Int
    get() = get(Calendar.YEAR)

val Calendar.hour: Int
    get() = get(Calendar.HOUR)

val Calendar.minute: Int
    get() = get(Calendar.MINUTE)
