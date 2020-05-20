package com.inchka.taptap

import android.app.Application
import android.content.Context

/**
 * Created by Grigory Azaryan on 5/18/20.
 */

class App : Application() {

    override fun onCreate() {
        super.onCreate()


        appContext = this
//        appComponent = DaggerAppComponent.builder()
////            .appModule(AppModule(appContext))
//            .build()
    }

    companion object App {
        lateinit var appContext: Context
        val appComponent: AppComponent by lazy {
            DaggerAppComponent.builder()
                .appModule(AppModule(appContext))
                .build()
        }
    }
}