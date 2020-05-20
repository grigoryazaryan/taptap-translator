package com.inchka.taptap

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Created by Grigory Azaryan on 5/18/20.
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()


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