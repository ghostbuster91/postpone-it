package io.github.ghostbuster91.postponeit.job.create.contacts

import android.database.Cursor
import android.provider.ContactsContract

class ContactCursorAdapter(private val cursor: Cursor) : Cursor by cursor {

    val photoThumbnailUri: String?
        get() {
            return getString(getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI))
        }

    val hasPhoneNumber: Boolean
        get() {
            return getInt(getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0
        }

    val displayName: String
        get() {
            return getString(getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
        }

    val contactId: String
        get() {
            return getString(getColumnIndexOrThrow(ContactsContract.Contacts._ID))
        }
}
