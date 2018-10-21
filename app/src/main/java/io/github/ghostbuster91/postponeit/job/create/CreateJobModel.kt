package io.github.ghostbuster91.postponeit.job.create

import android.annotation.SuppressLint
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.github.ghostbuster91.postponeit.job.AppEvent
import io.github.ghostbuster91.postponeit.job.create.JobCreationEvent.*
import io.reactivex.Observable
import io.reactivex.Observable.just
import io.reactivex.Observable.merge
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.withLatestFrom
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

@SuppressLint("CheckResult")
fun createJobCreationModel(appEvents: Observable<AppEvent>, contactList: List<Contact>): JobCreationModel {
    val localEvents = PublishRelay.create<JobCreationEvent>()
    val showTimePicker = merge(listOf(
            localEvents.filter { it is TimeInputClicked }.map { true },
            localEvents.filter { it is TimePickerDismissed }.map { false }))
            .startWith(false)
    val showDatePicker = merge(listOf(
            localEvents.filter { it is DateInputClicked }.map { true },
            localEvents.filter { it is DatePickerDismissed }.map { false }))
            .startWith(false)
    val selectedContactListState = BehaviorRelay.createDefault<List<Contact>>(emptyList())
    merge(listOf(localEvents.ofType(ContactSelected::class.java)
            .map { it.contact }
            .withLatestFrom(selectedContactListState)
            .map { (a, b) -> b + a },
            localEvents.ofType(SelectedContactClicked::class.java)
                    .map { it.selectedContact }
                    .withLatestFrom(selectedContactListState)
                    .map { (a, b) -> b - a }))
            .subscribe(selectedContactListState)
    val contactListState = just(contactList)
    val sendTime = localEvents.ofType(TimePicked::class.java).map { it.time }.startWith(LocalTime.now())
    val sendDate = localEvents.ofType(DatePicked::class.java).map { it.date }.startWith(LocalDate.now())
    val showPermissionError = just(false)
    val clearSendToText = merge(listOf(
            localEvents.ofType(ContactSelected::class.java).map { true },
            localEvents.ofType(SendToTextChanged::class.java).map { false }))
            .startWith(false)

    return JobCreationModel(
            localEvents,
            Observables.combineLatest(
                    selectedContactListState,
                    contactListState,
                    sendTime,
                    sendDate,
                    showTimePicker,
                    showDatePicker,
                    showPermissionError,
                    clearSendToText,
                    ::JobCreationState
            ))
}

data class JobCreationModel(val events: Consumer<JobCreationEvent>, val state: Observable<JobCreationState>)

data class JobCreationState(val sentToContacts: List<Contact>,
                            val contacts: List<Contact>,
                            val time: LocalTime,
                            val date: LocalDate,
                            val showTimePicker: Boolean,
                            val showDatePicker: Boolean,
                            val showPermissionError: Boolean,
                            val clearSendToText: Boolean)

sealed class JobCreationEvent {
    data class ContactSelected(val contact: Contact) : JobCreationEvent()
    data class DatePicked(val date: LocalDate) : JobCreationEvent()
    data class TimePicked(val time: LocalTime) : JobCreationEvent()
    data class SelectedContactClicked(val selectedContact: Contact) : JobCreationEvent()
    data class SendToTextChanged(val text: String) : JobCreationEvent()
    data class MessageTextChanged(val text: String) : JobCreationEvent()
    object TimeInputClicked : JobCreationEvent()
    object TimePickerDismissed : JobCreationEvent()
    object DateInputClicked : JobCreationEvent()
    object DatePickerDismissed : JobCreationEvent()
    object SubmitButtonClicked : JobCreationEvent()
}