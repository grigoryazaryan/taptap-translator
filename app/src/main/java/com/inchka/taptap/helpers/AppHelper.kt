package com.inchka.taptap.helpers

import android.content.Context
import android.content.SharedPreferences
import com.inchka.taptap.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Grigory Azaryan on 5/19/20.
 */

@Singleton
class AppHelper @Inject constructor(private val context: Context) {
    val TAG = "AppHelper"


    fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
    }

    var sourceLang: String?
        get() = getSharedPreferences().getString(Constants.SOURCE_LANG, null)
        set(value) = getSharedPreferences().edit().putString(Constants.SOURCE_LANG, value).apply()

    var targetLang: String?
        get() = getSharedPreferences().getString(Constants.TARGET_LANG, null)
        set(value) = getSharedPreferences().edit().putString(Constants.TARGET_LANG, value).apply()
}