package io.github.ghostbuster91.postponeit.job

import android.annotation.SuppressLint
import com.jakewharton.rxrelay2.PublishRelay
import com.trello.rxlifecycle2.LifecycleProvider
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.github.ghostbuster91.postponeit.CurrentActivityProvider
import io.github.ghostbuster91.postponeit.job.create.CreateJobActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer

@SuppressLint("CheckResult")
fun createAppModel(jobList: List<DelayedJob>): AppModel {
    val events = PublishRelay.create<AppEvent>()
    val commands = PublishRelay.create<AppCommand>()
    events.filter { it is AppEvent.CreateJobClicked }
            .map { AppCommand.Navigate(Screen.CREATE_NEW_JOB) }
            .ofType(AppCommand::class.java)
            .map { it }
            .subscribe(commands)
    return AppModel(Observable.just(jobList), Observable.never(), events, commands)
}


data class AppModel(val jobList: Observable<List<DelayedJob>>,
                    val jobDetails: Observable<DelayedJob>,
                    val events: PublishRelay<AppEvent>,
                    val commands: Observable<AppCommand>)

sealed class AppEvent {
    class JobCanceled(val id: String) : AppEvent()
    object CreateJobClicked : AppEvent()
}

sealed class AppCommand {
    class Navigate(val target: Screen) : AppCommand()
}

@SuppressLint("CheckResult")
fun <T, S> LifecycleProvider<T>.bind(
        provider: () -> Pair<Consumer<AppEvent>, Observable<S>>,
        view: ReactView<S>
) {
    val appModel = provider()
    appModel.second
            .observeOn(AndroidSchedulers.mainThread())
            .bindToLifecycle(this)
            .subscribe(view::render)
    view.events
            .bindToLifecycle(this)
            .subscribe(appModel.first)
}

interface ReactView<S> {
    val events: Observable<AppEvent>
    fun render(s: S)
}

enum class Screen {
    CREATE_NEW_JOB,
    JOB_LIST
}

@SuppressLint("CheckResult")
fun navigator(navigationCommands: Observable<AppCommand>) {
    navigationCommands
            .observeOn(AndroidSchedulers.mainThread())
            .ofType(AppCommand.Navigate::class.java)
            .subscribe {
                CurrentActivityProvider.currentActivity?.let { context ->
                    when (it.target) {
                        Screen.CREATE_NEW_JOB -> CreateJobActivity.start(context)
                        Screen.JOB_LIST -> TODO()
                    }
                }
            }
}