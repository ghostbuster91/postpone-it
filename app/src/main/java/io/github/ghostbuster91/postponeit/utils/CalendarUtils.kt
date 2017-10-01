package io.github.ghostbuster91.postponeit.utils

import java.util.*

var Calendar.day: Int
    get() = get(Calendar.DAY_OF_MONTH)
    set(value) = set(Calendar.DAY_OF_MONTH, value)

var Calendar.month: Int
    get() = get(Calendar.MONTH)
    set(value) = set(Calendar.MONTH, value)

var Calendar.year: Int
    get() = get(Calendar.YEAR)
    set(value) = set(Calendar.YEAR, value)

var Calendar.hour: Int
    get() = get(Calendar.HOUR)
    set(value) = set(Calendar.HOUR, value)

var Calendar.minute: Int
    get() = get(Calendar.MINUTE)
    set(value) = set(Calendar.MINUTE, value)

fun Calendar.toDate() = Date(timeInMillis)