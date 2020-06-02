package com.inchka.translator.helpers

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.inchka.translator.BuildConfig
import com.inchka.translator.model.Lang
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
        set(value) = getSharedPreferences().edit().putString(Constants.FIRST_LANG, value).apply()

    var secondLang: String?
        get() = getSharedPreferences().getString(Constants.SECOND_LANG, null)
        set(value) = getSharedPreferences().edit().putString(Constants.SECOND_LANG, value).apply()


    fun mostProbableSecondLanguage(detectedSourceLang: Lang): Lang {
        val first = firstLang?.let { Lang.valueOf(it) }
        val second = secondLang?.let { Lang.valueOf(it) }

        return if (first != null && second != null) {
            if (detectedSourceLang == first)
                second else first
        } else { // assume no languages are known yet
            if (detectedSourceLang != Lang.EN)
                Lang.EN else Lang.ES
        }
    }

    fun copyToClipboard(text: String, label: String? = null) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }

    fun showToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}