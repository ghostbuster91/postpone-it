package io.github.ghostbuster91.postponeit.job.create

import android.Manifest
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.support.design.widget.TextInputEditText
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithLayoutAndBinder
import com.elpassion.android.commons.recycler.basic.BasicAdapter
import com.github.salomonbrys.kodein.LazyKodein
import com.github.salomonbrys.kodein.LazyKodeinAware
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.jakewharton.rxbinding2.view.clicks
import com.tbruyelle.rxpermissions2.RxPermissions
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import io.github.ghostbuster91.postponeit.R
import io.github.ghostbuster91.postponeit.job.JobService
import io.github.ghostbuster91.postponeit.utils.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.create_job_layout.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class CreateJobActivity : RxAppCompatActivity(), LazyKodeinAware {
    override val kodein: LazyKodein = LazyKodein(appKodein)
    private val jobService by instance<JobService>()
    private val disposable = CompositeDisposable()
    private var contactsAdapter: ContactsAdapter? = null
    private val selectedContactsAdapter by lazy(LazyThreadSafetyMode.NONE, this::createSelectedContactsAdapter)
    private val selectedTime = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_job_layout)
        setSupportActionBar(toolbar)
        val rxPermissions = RxPermissions(this)
        addScheduleButtonClickListener(rxPermissions)
        readContactsFromPhone(rxPermissions)
        selectedContactsView.adapter = selectedContactsAdapter
        selectedContactsView.layoutManager = FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP)
        initTimePicker(selectedTime)
        initDatePicker(selectedTime)
        contactSelector.setOnItemClickListener { parent, view, position, id ->
            contactSelector.setText("")
            val selectedContact = contactsAdapter!!.getItem(position)
            selectedContactsAdapter.items = selectedContactsAdapter.items + selectedContact
            selectedContactsAdapter.notifyDataSetChanged()
        }
    }

    private fun createSelectedContactsAdapter() =
            basicAdapterWithLayoutAndBinder(
                    mutableListOf<Contact>(),
                    R.layout.create_job_selected_contact_item,
                    { holder, item ->
                        (holder.itemView as TextView).text = item.label
                        holder.itemView.setOnClickListener {
                            removeSelectedContact(item)
                        }
                    })

    private fun removeSelectedContact(item: Contact) {
        selectedContactsAdapter.items = selectedContactsAdapter.items - item
        selectedContactsAdapter.notifyDataSetChanged()
    }

    private fun readContactsFromPhone(rxPermissions: RxPermissions) {
        val readContactsPermission = rxPermissions
                .request(Manifest.permission.READ_CONTACTS)
                .share()
        displayContactList(readContactsPermission)
        showPermissionNeededInfo(readContactsPermission)
    }

    private fun addScheduleButtonClickListener(rxPermissions: RxPermissions) {
        val requestSmsPermission = rxPermissions.request(Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE)
        scheduleButton.clicks()
                .bindToLifecycle(this)
                .flatMap { requestSmsPermission }
                .subscribe { granted ->
                    if (granted) {
                        scheduleSendingSms()
                    } else {
                        Toast.makeText(this, "not granted", Toast.LENGTH_LONG).show()
                    }
                }
    }

    private fun displayContactList(readContactsPermission: Observable<Boolean>) {
        readContactsPermission
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .filter { it }
                .map { getContactList() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    contactsAdapter = ContactsAdapter(this, it, selectedContactsAdapter::items)
                    contactSelector.setAdapter(contactsAdapter)
                    contactSelector.validator = ContactAdapterValidator(it)
                }
                .let { disposable.add(it) }
    }

    private fun showPermissionNeededInfo(readContactsPermission: Observable<Boolean>) {
        readContactsPermission
                .filter { !it }
                .subscribe {
                    Toast.makeText(this, "not granted", Toast.LENGTH_LONG).show()
                }
                .let { disposable.add(it) }
    }

    private fun initDatePicker(calendar: Calendar) {
        with(calendar) {
            setNewDate(day, month, year)
            dateInput.setOnClickListener {
                showDatePicker(year, month, day)
            }
        }
    }

    private fun initTimePicker(calendar: Calendar) {
        with(calendar) {
            setNewTime(calendar.toDate())
            timeInput.setOnClickListener {
                showTimePicker(hour, minute)
            }
        }
    }

    private fun showDatePicker(currentYear: Int, currentMonth: Int, currentDay: Int) {
        val dpd = DatePickerDialog.newInstance(
                { _, y, m, d ->
                    selectedTime.apply {
                        day = d
                        month = m
                        year = y
                    }
                    setNewDate(d, m, y)
                },
                currentYear,
                currentMonth,
                currentDay)
        dpd.show(fragmentManager, "Datepickerdialog")
    }

    private fun setNewDate(d: Int, m: Int, y: Int) {
        dateInput.setText(getString(R.string.create_job_date_format, d.leadingZero(), (m + 1).leadingZero(), y.leadingZero()))
    }

    private fun showTimePicker(currentHour: Int, currentMinute: Int) {
        val dpd = TimePickerDialog.newInstance(
                { _, h, m, _ ->
                    setNewTime(
                            selectedTime.apply {
                                hour = h
                                minute = m
                            }.toDate())
                },
                currentHour,
                currentMinute,
                true)
        dpd.show(fragmentManager, "TimepickerDialog")
    }

    private fun setNewTime(time: Date) {
        val timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT, resources.configuration.locale)
        timeInput.setText(timeFormat.format(time))
    }

    private fun Int.leadingZero() = String.format("%02d", this)

    private fun scheduleSendingSms() {
        val isValidationOk = listOf(
                selectedContactAdapterValidator(contactSelector, selectedContactsAdapter, "Provide at least one contact"),
                emptyValidator(smsTextInput, "Text cannot be empty"),
                emptyValidator(timeInput, "Time cannot be empty"),
                emptyValidator(dateInput, "Date cannot be empty"))
                .all { it }
        if (isValidationOk) {
            val timeInMillis = selectedTime.timeInMillis
            selectedContactsAdapter.items.forEach {
                jobService.createJob(timeInMillis, smsTextInput.text.toString(), it.phoneNumber)
            }
            finish()
        }
    }

    private fun emptyValidator(input: TextInputEditText, errorMessage: String): Boolean {
        return if (input.text.isBlank()) {
            input.error = errorMessage
            false
        } else {
            input.error = null
            true
        }
    }

    private fun selectedContactAdapterValidator(input: EditText, selectedContactsAdapter: BasicAdapter<Contact>, errorMessage: String): Boolean {
        return if (selectedContactsAdapter.items.isEmpty()) {
            input.error = errorMessage
            false
        } else {
            input.error = null
            true
        }
    }

    private fun getContactList(): MutableList<Contact> {
        return contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
                .use { contactCursor ->
                    createContactList(contactCursor)
                }
    }

    private fun createContactList(contactCursor: Cursor): MutableList<Contact> {
        val contacts = mutableListOf<Contact>()
        while (contactCursor.moveToNext()) {
            val id = contactCursor.contactId
            val name = contactCursor.displayName
            val avatarUri = contactCursor.photoThumbnailUri
            if (contactCursor.hasPhoneNumber) {
                val numbers = getPhoneNumbers(id)
                numbers.forEach {
                    val contactChip = Contact(id = id, avatarUri = avatarUri, label = name, phoneNumber = it)
                    contacts.add(contactChip)
                }
            }
        }
        return contacts
    }

    private fun getPhoneNumbers(contactId: String): MutableList<String> {
        val numbers = mutableListOf<String>()
        contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(contactId), null)
                .use { phoneCursor ->
                    while (phoneCursor.moveToNext()) {
                        numbers.add(phoneCursor.phoneNumber)
                    }
                }
        return numbers
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, CreateJobActivity::class.java))
        }
    }
}

private val Cursor.photoThumbnailUri: String?
    get() {
        return getString(getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI))
    }

private val Cursor.hasPhoneNumber: Boolean
    get() {
        return getInt(getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0
    }

private val Cursor.phoneNumber: String
    get() {
        return getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
    }

private val Cursor.displayName: String
    get() {
        return getString(getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
    }

private val Cursor.contactId: String
    get() {
        return getString(getColumnIndexOrThrow(ContactsContract.Contacts._ID))
    }
