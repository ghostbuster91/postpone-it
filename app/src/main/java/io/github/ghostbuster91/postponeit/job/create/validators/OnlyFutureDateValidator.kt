package io.github.ghostbuster91.postponeit.job.create.validators

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.github.ghostbuster91.postponeit.job.create.Validator
import org.threeten.bp.Instant
import java.util.*

class OnlyFutureDateValidator(private val selectedTime: Calendar,
                              private val appCompatActivity: AppCompatActivity) : Validator {

    class OnlyFutureDateSupportedDialog : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return AlertDialog.Builder(activity)
                    .setTitle("Illegal operation!")
                    .setMessage("Sending messages into the past is not YET supported.")
                    .create()
        }
    }

    override fun validate(): Boolean {
        if (Instant.now().toEpochMilli() > selectedTime.timeInMillis) {
            OnlyFutureDateSupportedDialog().show(appCompatActivity.fragmentManager, "TAG")
            return false
        } else {
            return true
        }
    }
}