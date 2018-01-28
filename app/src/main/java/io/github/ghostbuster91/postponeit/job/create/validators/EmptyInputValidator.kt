package io.github.ghostbuster91.postponeit.job.create.validators

import android.widget.EditText
import io.github.ghostbuster91.postponeit.job.create.Validator

class EmptyInputValidator(private val input: EditText,
                          private val errorMessage: String) : Validator {
    override fun validate(): Boolean {
        return if (input.text.isBlank()) {
            input.error = errorMessage
            false
        } else {
            input.error = null
            true
        }
    }
}