package io.github.ghostbuster91.postponeit.job.common

import android.support.design.widget.TextInputEditText
import io.github.ghostbuster91.postponeit.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun TextInputEditText.setTimeText(time: Date) {
    val timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT, resources.configuration.locale)
    setText(timeFormat.format(time))
}

fun TextInputEditText.setDateText(d: Int, m: Int, y: Int) {
    setText(context.getString(R.string.create_job_date_format, d.leadingZero(), (m + 1).leadingZero(), y.leadingZero()))
}

private fun Int.leadingZero() = String.format("%02d", this)

