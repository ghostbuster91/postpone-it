package io.github.ghostbuster91.postponeit.job.create

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.jakewharton.rxbinding2.widget.text
import com.jakewharton.rxbinding2.widget.textChanges
import com.jakewharton.rxrelay2.PublishRelay
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import io.github.ghostbuster91.postponeit.R
import io.github.ghostbuster91.postponeit.job.AppModel
import io.github.ghostbuster91.postponeit.job.ReactView
import io.github.ghostbuster91.postponeit.job.bind
import io.github.ghostbuster91.postponeit.job.common.dateToText
import io.github.ghostbuster91.postponeit.job.common.timeToText
import io.github.ghostbuster91.postponeit.job.create.contacts.getContactList
import io.reactivex.Observable
import io.reactivex.functions.Function
import kotlinx.android.synthetic.main.create_job_layout.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

class CreateJobActivity : RxAppCompatActivity(), LazyKodeinAware, ReactView<JobCreationState, JobCreationEvent> {
    override val kodein: LazyKodein = LazyKodein(appKodein)
    private var contactsAdapter: ContactsAdapter? = null
    private val appModel by instance<AppModel>()
    private val selectedContactsAdapter by lazy(LazyThreadSafetyMode.NONE, this::createSelectedContactsAdapter)
    private val localEvents = PublishRelay.create<JobCreationEvent>()
    override val events: Observable<JobCreationEvent> by lazy {
        Observable.merge(
                listOf(
                        dateInput.clicks().map { JobCreationEvent.DateInputClicked },
                        timeInput.clicks().map { JobCreationEvent.TimeInputClicked },
                        scheduleButton.clicks().map { JobCreationEvent.SubmitButtonClicked },
                        smsTextInput.textChanges().map { JobCreationEvent.MessageTextChanged(it.toString()) },
                        contactSelector.textChanges().map { JobCreationEvent.SendToTextChanged(it.toString()) },
                        localEvents))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_job_layout)
        setSupportActionBar(toolbar)
        initContactSelector()
        bind({
            val createJobCreationModel = createJobCreationModel(appModel.events, getContactList(contentResolver))
            createJobCreationModel.events to createJobCreationModel.state
        }, this)
    }

    override fun render(state: Observable<JobCreationState>) {
        state
                .publish { state ->
                    state.map { timeToText(it.time) }.distinctUntilChanged().subscribe(timeInput.text())
                    state.map { dateToText(it.date) }.distinctUntilChanged().subscribe(dateInput.text())
                    state.map { it.clearSendToText }.distinctUntilChanged().filter { it }.subscribe { contactSelector.setText("") }
                    state.distinctUntilChanged(Function<JobCreationState, Boolean> { it.showDatePicker }).filter { it.showDatePicker }.subscribe { showDatePicker(it.date) }
                    state.distinctUntilChanged(Function<JobCreationState, Boolean> { it.showTimePicker }).filter { it.showTimePicker }.subscribe { showTimePicker(it.time) }
                    state.map { it.sentToContacts }.distinctUntilChanged().subscribe {
                        selectedContactsAdapter.items = it
                        selectedContactsAdapter.notifyDataSetChanged()
                    }
                    state.map { it.contacts }.distinctUntilChanged().subscribe { displayContactList(it) }
                    state.map { it.showPermissionError }.distinctUntilChanged().filter { it }.subscribe {
                        Toast.makeText(this, "not granted", Toast.LENGTH_LONG).show()
                    }
                    state
                }
                .subscribe()
    }

    private fun initContactSelector() {
        selectedContactsView.adapter = selectedContactsAdapter
        selectedContactsView.layoutManager = FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP)
        contactSelector.setOnItemClickListener { parent, view, position, id ->
            val selectedContact = contactsAdapter!!.getItem(position)!!
            localEvents.accept(JobCreationEvent.ContactSelected(selectedContact))
        }
    }

    private fun createSelectedContactsAdapter(): BasicAdapter<Contact> {
        return basicAdapterWithLayoutAndBinder(mutableListOf(), R.layout.create_job_selected_contact_item) { holder, item ->
            (holder.itemView as TextView).text = item.label
            holder.itemView.setOnClickListener {
                localEvents.accept(JobCreationEvent.SelectedContactClicked(item))
            }
        }
    }

//    private fun addScheduleButtonClickListener(rxPermissions: RxPermissions) {
//        val requestSmsPermission = rxPermissions.request(Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_STATE)
//        scheduleButton.clicks()
//                .bindToLifecycle(this)
//                .flatMap { requestSmsPermission }
//                .subscribe { granted ->
//                    if (granted) {
//                        scheduleSendingSms()
//                    } else {
//                    }
//                }
//    }

    private fun displayContactList(contacts: List<Contact>) {
        contactsAdapter = ContactsAdapter(this, contacts, selectedContactsAdapter::items)
        contactSelector.setAdapter(contactsAdapter)
        contactSelector.validator = ContactAdapterValidator(contacts)
    }

    private fun showDatePicker(date: LocalDate) {
        val dpd = DatePickerDialog.newInstance(
                { _, y, m, d ->
                    localEvents.accept(JobCreationEvent.DatePicked(LocalDate.of(y, m, d)))
                },
                date.year,
                date.monthValue,
                date.dayOfMonth)
        dpd.setOnDismissListener { localEvents.accept(JobCreationEvent.DatePickerDismissed) }
        dpd.show(fragmentManager, "Datepickerdialog")
    }

    private fun showTimePicker(time: LocalTime) {
        val dpd = TimePickerDialog.newInstance(
                { _, h, m, _ ->
                    localEvents.accept(JobCreationEvent.TimePicked(LocalTime.of(h, m)))
                },
                time.hour,
                time.minute,
                true)
        dpd.setOnDismissListener { localEvents.accept(JobCreationEvent.TimePickerDismissed) }
        dpd.show(fragmentManager, "TimepickerDialog")
    }

//    private fun scheduleSendingSms() {
//        val isValidationOk = listOf(
//                SelectedContactAdapterValidator(contactSelector, selectedContactsAdapter, "Provide at least one contact"),
//                EmptyInputValidator(smsTextInput, "Text cannot be empty"),
//                EmptyInputValidator(timeInput, "Time cannot be empty"),
//                EmptyInputValidator(dateInput, "Date cannot be empty"),
//                OnlyFutureDateValidator(selectedTime, this))
//                .all { it.validate() }
//        if (isValidationOk) {
//            val timeInMillis = selectedTime.timeInMillis
//            selectedContactsAdapter.items.forEach {
//                jobService.createJob(timeInMillis, smsTextInput.text.toString(), it, requiresAcceptanceView.isChecked)
//            }
//            finish()
//        }
//    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, CreateJobActivity::class.java))
        }
    }
}