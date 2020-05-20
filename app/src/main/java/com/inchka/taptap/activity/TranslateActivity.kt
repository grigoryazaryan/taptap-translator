package com.inchka.taptap.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.inchka.taptap.App
import com.inchka.taptap.DeepL
import com.inchka.taptap.Lang
import com.inchka.taptap.R
import com.inchka.taptap.helpers.AppHelper
import kotlinx.android.synthetic.main.activity_translate.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TranslateActivity : AppCompatActivity() {
    private val TAG = "TranslateActivity"

    @Inject
    lateinit var deepL: DeepL

    @Inject
    lateinit var appHelper: AppHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translate)
        App.appComponent.inject(this)

        background.setOnClickListener { finish() }

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun translate(sourceLang: Lang? = null, targetLang: Lang, text: String) {
        launchCoroutine {
            try {
                val resp = deepL.translate(text = text, target_lang = targetLang)
                Log.v(TAG, "${resp.toString()}")
                resp.takeIf { it.translations.isNotEmpty() }
                    ?.let { it.translations }
                    ?.first()
                    ?.let {
                        withContext(Dispatchers.Main) {
                            translated_text.setText(it.text)
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun handleIntent(intent: Intent?) {
        Log.v(TAG, "handleIntent $intent")
        intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.let {
            source_text.setText(it)
//            translate(targetLang = Lang.RU, text = it.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        Log.v(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.v(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.v(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.v(TAG, "onStop")
    }
}
