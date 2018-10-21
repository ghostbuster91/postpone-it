package io.github.ghostbuster91.postponeit.job

import android.Manifest
import android.annotation.SuppressLint
import com.jakewharton.rxrelay2.PublishRelay
import com.tbruyelle.rxpermissions2.RxPermissions
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.github.ghostbuster91.postponeit.CurrentActivityProvider
import io.github.ghostbuster91.postponeit.job.create.CreateJobActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer

@SuppressLint("CheckResult")
fun createAppModel(jobList: List<DelayedJob>, permissionStream: Observable<PermissionEvent>): AppModel {
    val events = PublishRelay.create<AppEvent>()
    val commands = PublishRelay.create<AppCommand>()
    val navigateStream = events.filter { it is AppEvent.CreateJobClicked }
            .doOnNext { commands.accept(AppCommand.RequestPermission(Manifest.permission.READ_CONTACTS)) }
            .flatMap { _ -> permissionStream.filter { it.permission == Manifest.permission.READ_CONTACTS } }
            .map {
                if (it is PermissionEvent.PermissionGranted) {
                    AppCommand.Navigate(Screen.CREATE_NEW_JOB)
                } else {
                    AppCommand.Notify("Permission not granted!")
                }
            }
    navigateStream
            .ofType(AppCommand::class.java)
            .subscribe(commands)
    return AppModel(Observable.just(jobList), Observable.never(), events, commands)
}


data class AppModel(val jobList: Observable<List<DelayedJob>>,
                    val jobDetails: Observable<DelayedJob>,
                    val events: PublishRelay<AppEvent>,
                    val commands: Observable<AppCommand>)

sealed class AppEvent {
    data class JobCanceled(val id: String) : AppEvent()
    object CreateJobClicked : AppEvent()
}

sealed class AppCommand {
    class Navigate(val target: Screen) : AppCommand()
    class RequestPermission(val permission: String) : AppCommand()
    class Notify(val message:String): AppCommand()
}

@SuppressLint("CheckResult")
fun <T, S, E> LifecycleProvider<T>.bind(
        provider: () -> Pair<Consumer<E>, Observable<S>>,
        view: ReactView<S, E>
) {
    val appModel = provider()
    view.render(appModel.second
            .observeOn(AndroidSchedulers.mainThread())
            .bindToLifecycle(this))
    view.events
            .bindToLifecycle(this)
            .subscribe(appModel.first)
}

interface ReactView<S, E> {
    val events: Observable<E>
    fun render(state: Observable<S>)
}

enum class Screen {
    CREATE_NEW_JOB,
    JOB_LIST
}

@SuppressLint("CheckResult")
private fun navigator(navigationCommands: Observable<AppCommand.Navigate>) {
    navigationCommands
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                CurrentActivityProvider.currentActivity?.let { context ->
                    when (it.target) {
                        Screen.CREATE_NEW_JOB -> CreateJobActivity.start(context)
                        Screen.JOB_LIST -> TODO()
                    }
                }
            }
}

fun commandExecutor(appCommands: Observable<AppCommand>, permissionConsumer: Consumer<in PermissionEvent>) {
    navigator(appCommands.ofType(AppCommand.Navigate::class.java))
    permissionRequester(appCommands.ofType(AppCommand.RequestPermission::class.java), permissionConsumer)
}

@SuppressLint("CheckResult")
private fun permissionRequester(requestPermissionCommands: Observable<AppCommand.RequestPermission>, appEvents: Consumer<in PermissionEvent>) {
    requestPermissionCommands
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { request ->
                (CurrentActivityProvider.currentActivity?.let { context -> RxPermissions(context) }
                        ?.request(request.permission) ?: Observable.just(false))
                        .map {
                            if (it) {
                                PermissionEvent.PermissionGranted(request.permission)
                            } else {
                                PermissionEvent.PermissionDenied(request.permission)
                            }
                        }
                        .subscribe(appEvents)
            }
}

sealed class PermissionEvent {
    abstract val permission: String

    data class PermissionGranted(override val permission: String) : PermissionEvent()
    data class PermissionDenied(override val permission: String) : PermissionEvent()
}