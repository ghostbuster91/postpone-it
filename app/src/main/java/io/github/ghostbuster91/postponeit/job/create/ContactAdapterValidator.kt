package io.github.ghostbuster91.postponeit.job.create

import android.widget.AutoCompleteTextView

class ContactAdapterValidator(private val contacts: List<Contact>) : AutoCompleteTextView.Validator {
    override fun fixText(invalidText: CharSequence?) = ""

    override fun isValid(text: CharSequence) =
            contacts.any { it.label == text } || text.isEmpty()
}