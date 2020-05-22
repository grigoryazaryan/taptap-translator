package com.inchka.taptap.activity

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.inchka.taptap.model.Lang
import com.inchka.taptap.model.label

/**
 * Created by Grigory Azaryan on 5/19/20.
 */

/**
 * Create and launch Activity lifecycle aware coroutine
 */
fun AppCompatActivity.showLangPickerDialog(defaultLang: Lang?=null, langSelectedListener: (langSelected: Lang) -> Unit) {
    val langs: Array<Lang> = Lang.values()
    val items = langs.map { "${it} (${it.label()})" }.toTypedArray()

    val dialog = MaterialAlertDialogBuilder(this)
        .setSingleChoiceItems(items, langs.indexOf(defaultLang)) { d, which ->

            langSelectedListener(langs[which])
            d.dismiss()
        }
        .show()

    lifecycle.addObserver(object : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy() {
            dialog.dismiss()
            lifecycle.removeObserver(this)
        }
    })
}