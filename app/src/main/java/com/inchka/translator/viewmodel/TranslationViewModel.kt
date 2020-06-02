package com.inchka.translator.viewmodel

import android.os.Bundle
import androidx.lifecycle.*
import timber.log.Timber


class TranslationViewModel(private val state: SavedStateHandle) : ViewModel() {
    private val currentFilter = state.getLiveData("currentFilter", "initial value")
    fun currentFilter(): LiveData<String> = currentFilter

    val data = liveData<Ev<*>> {
        Timber.v("ttt livedata")
        emit(Ev("string"))
    }

    fun saveState(bundle: Bundle) {
        bundle.putString("currentFilter", currentFilter.value)
    }

    fun restoreState(bundle: Bundle) {
        currentFilter.value =
            bundle.getString("currentFilter")
    }
}

class Ev<out T>(val data: T) {

}