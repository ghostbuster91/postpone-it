package io.github.ghostbuster91.postponeit.job.create.validators

import android.widget.EditText
import com.elpassion.android.commons.recycler.basic.BasicAdapter
import io.github.ghostbuster91.postponeit.job.create.Contact
import io.github.ghostbuster91.postponeit.job.create.Validator

class SelectedContactAdapterValidator(private val input: EditText,
                                      private val selectedContactsAdapter: BasicAdapter<Contact>,
                                      private val errorMessage: String) : Validator {
    override fun validate(): Boolean {
        return if (selectedContactsAdapter.items.isEmpty()) {
            input.error = errorMessage
            false
        } else {
            input.error = null
            true
        }
    }
}