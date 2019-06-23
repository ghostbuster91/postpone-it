package io.github.ghostbuster91.postponeit.job.common

import android.app.Activity
import com.google.android.material.textfield.TextInputEditText
import io.github.ghostbuster91.postponeit.R
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun TextInputEditText.setTimeText(time: Date) {
    val timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT, resources.configuration.locale)
    setText(timeFormat.format(time))
}

fun Activity.timeToText(time: LocalTime): String {
    return DateTimeFormatter.ISO_TIME.format(time)
}


fun Activity.dateToText(date: LocalDate): String {
    return getString(R.string.create_job_date_format, date.dayOfMonth.leadingZero(), (date.monthValue + 1).leadingZero(), date.year.leadingZero())
}

private fun Int.leadingZero() = String.format("%02d", this)

