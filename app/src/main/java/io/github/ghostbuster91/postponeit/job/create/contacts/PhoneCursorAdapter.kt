package io.github.ghostbuster91.postponeit.job.create.contacts

import android.database.Cursor
import android.provider.ContactsContract

class PhoneCursorAdapter(private val cursor: Cursor) : Cursor by cursor {

    val phoneNumber: String
        get() {
            return getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
        }
}