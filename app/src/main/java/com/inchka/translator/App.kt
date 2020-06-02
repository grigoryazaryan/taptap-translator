package com.inchka.translator

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/**
 * Created by Grigory Azaryan on 5/18/20.
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()

        // enable crash reports for release app only
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }

    companion object {
        lateinit var appComponent: AppComponent
    }
}