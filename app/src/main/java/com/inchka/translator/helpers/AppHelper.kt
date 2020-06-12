package com.inchka.translator.helpers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.inchka.translator.BuildConfig
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

    var firstLang: String?
        get() = getSharedPreferences().getString(Constants.FIRST_LANG, null)
        set(value) {
            Firebase.analytics.setUserProperty(Constants.FIRST_LANG, value)
            getSharedPreferences().edit().putString(Constants.FIRST_LANG, value).apply()
        }

    var secondLang: String?
        get() = getSharedPreferences().getString(Constants.SECOND_LANG, null)
        set(value) {
            Firebase.analytics.setUserProperty(Constants.SECOND_LANG, value)
            getSharedPreferences().edit().putString(Constants.SECOND_LANG, value).apply()
        }


    var introPlayed: Boolean
        get() = getSharedPreferences().getBoolean(Constants.INTRO_SEEN, false)
        set(value) = getSharedPreferences().edit().putBoolean(Constants.INTRO_SEEN, value).apply()

    fun copyToClipboard(text: String, label: String? = null) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }

    fun showToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}