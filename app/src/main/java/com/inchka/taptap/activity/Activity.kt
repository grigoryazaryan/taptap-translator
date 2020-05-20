package com.inchka.taptap.activity

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Grigory Azaryan on 5/19/20.
 */

/**
 * Create and launch Activity lifecycle aware coroutine
 */
fun AppCompatActivity.launchActivityCoroutine(routine: suspend () -> Unit) {

    val job = CoroutineScope(Dispatchers.IO).launch {
        routine.invoke()
    }
    lifecycle.addObserver(object : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun destroy() {
            job.cancel()
        }
    })
}