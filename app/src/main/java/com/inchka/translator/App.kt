package com.inchka.translator

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.inchka.translator.helpers.Constants
import timber.log.Timber
import java.util.*

/**
 * Created by Grigory Azaryan on 5/18/20.
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()

        // enable crash reports for release app only
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        // initialize AdMob
        MobileAds.initialize(this)

        logUserProperties()
    }

    private fun logUserProperties() {
        // log user system language ang country to firebase
        Firebase.analytics.setUserProperty(Constants.COUNTRY, Locale.getDefault().displayCountry)
        Firebase.analytics.setUserProperty(Constants.OS_LANG, Locale.getDefault().language)
    }

    companion object {
        lateinit var appComponent: AppComponent
    }
}