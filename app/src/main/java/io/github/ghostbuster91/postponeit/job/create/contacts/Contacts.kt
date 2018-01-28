package io.github.ghostbuster91.postponeit.job.create.contacts

import android.content.ContentResolver
import android.provider.ContactsContract
import io.github.ghostbuster91.postponeit.job.create.Contact

fun getContactList(contentResolver: ContentResolver): MutableList<Contact> {
    return contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
            .let { ContactCursorAdapter(it) }
            .use { cursor ->
                createContactList(cursor, contentResolver)
            }
}

private fun createContactList(contactCursor: ContactCursorAdapter, contentResolver: ContentResolver): MutableList<Contact> {
    val contacts = mutableListOf<Contact>()
    while (contactCursor.moveToNext()) {
        val id = contactCursor.contactId
        val name = contactCursor.displayName
        val avatarUri = contactCursor.photoThumbnailUri
        if (contactCursor.hasPhoneNumber) {
            val numbers = getPhoneNumbers(id, contentResolver)
            numbers.forEach {
                val contactChip = Contact(id = id, avatarUri = avatarUri, label = name, phoneNumber = it)
                contacts.add(contactChip)
            }
        }
    }
    return contacts
}

private fun getPhoneNumbers(contactId: String, contentResolver: ContentResolver): MutableList<String> {
    val numbers = mutableListOf<String>()
    contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(contactId), null)
            .let { PhoneCursorAdapter(it) }
            .use { cursor ->
                while (cursor.moveToNext()) {
                    numbers.add(cursor.phoneNumber)
                }
            }
    return numbers
}