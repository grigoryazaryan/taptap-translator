package com.inchka.translator.helpers

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity

/**
 * Created by Grigory Azaryan on 5/19/20.
 */

class Utils {
    companion object {

    }
}

fun FragmentActivity.hideKeyboard() {
    currentFocus?.let { v ->
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }
}