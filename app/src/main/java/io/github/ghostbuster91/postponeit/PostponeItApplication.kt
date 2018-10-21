package io.github.ghostbuster91.postponeit

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.github.salomonbrys.kodein.*
import com.github.salomonbrys.kodein.android.androidModule
import com.google.gson.Gson
import io.fabric.sdk.android.Fabric
import io.github.ghostbuster91.postponeit.job.*
import io.github.ghostbuster91.postponeit.job.execute.NotificationService
import io.github.ghostbuster91.postponeit.job.execute.NotificationServiceImpl

class PostponeItApplication : Application(), KodeinAware {
    override val kodein: Kodein by Kodein.lazy {
        import(androidModule)
        bind<AlarmManagerService>() with singleton { AlarmManagerServiceImpl(applicationContext) }
        bind<NotificationService>() with singleton { NotificationServiceImpl(applicationContext) }
        bind<Gson>() with singleton { gsonProvider() }
        bind<JobRepository>() with singleton {
            JobRepositoryImpl(
                    sharedPreferences = factory<Context, SharedPreferences>()(applicationContext),
                    gson = instance())
        }
        bind<JobService>() with singleton {
            JobServiceImpl(alarmManagerService = instance(), jobRepository = instance())
        }
        bind<AppModel>() with singleton {
            createAppModel(emptyList())
        }
    }

    private val appModel = kodein.instance<AppModel>()

    override fun onCreate() {
        super.onCreate()
        val crashlyticsCore = CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG)
                .build()
        registerActivityLifecycleCallbacks(CurrentActivityProvider)
        navigator(appModel.commands)
        Fabric.with(this, Crashlytics.Builder().core(crashlyticsCore).build())
    }
}