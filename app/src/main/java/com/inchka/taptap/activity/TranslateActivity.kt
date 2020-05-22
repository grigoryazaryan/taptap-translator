package com.inchka.taptap.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.inchka.taptap.App
import com.inchka.taptap.R
import com.inchka.taptap.deepl.DeepL
import com.inchka.taptap.helpers.AppHelper
import com.inchka.taptap.model.Lang
import com.inchka.taptap.model.label
import kotlinx.android.synthetic.main.activity_translate.*
import kotlinx.coroutines.launch
import javax.inject.Inject


class TranslateActivity : AppCompatActivity() {
    private val TAG = "TranslateActivity"

    @Inject
    lateinit var deepL: DeepL

    @Inject
    lateinit var appHelper: AppHelper

    @Inject
    lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translate)
        App.appComponent.inject(this)

        background.setOnClickListener { finish() }

        copy.setOnClickListener {
            appHelper.copyToClipboard(translated_text.text.toString())
            appHelper.showToast(getString(R.string.copied_to_clipboard))
        }

//        button.setOnClickListener {
//            val originalText = original_text.text.toString()
//            (translate_to_lang.tag as? Lang)?.let { toLang ->
//                lifecycle.coroutineScope.launch {
//                    val (detectedLang, translatedText) = translate(toLang, originalText)
//                    setData(true, detectedLang, toLang, originalText, translatedText)
//                    appHelper.firstLang = detectedLang.toString()
//                    appHelper.secondLang = toLang.toString()
//                }
//            }
//        }

        translate_to_lang.setOnClickListener {
            val lang = translate_to_lang.tag as? Lang
            showLangPickerDialog(lang) { langSelected ->
                val originalText = original_text.text.toString()
                lifecycle.coroutineScope.launch {
                    val (detectedLang, translatedText) = translate(langSelected, originalText)
                    setData(true, detectedLang, langSelected, originalText, translatedText)
                    appHelper.firstLang = detectedLang.toString()
                    appHelper.secondLang = langSelected.toString()
                }
            }
        }

        intent.putExtra(Intent.EXTRA_PROCESS_TEXT, "Hello, world!")
//        intent.putExtra(Intent.EXTRA_PROCESS_TEXT, "привет, мир!")

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private data class TranslateResult(val detectedLang: Lang, val translatedText: String)

    private suspend fun translate(targetLang: Lang, text: String): TranslateResult {
        val resp = deepL.translate(text = text, target_lang = targetLang)
        val translation = resp.takeIf { it.translations.isNotEmpty() }
            ?.translations
            ?.first()!!

        return TranslateResult(translation.detected_source_language, translation.text)
    }

    fun setData(sourceLangAutoDetected: Boolean, sourceLang: Lang, targetLang: Lang, originalText: String, translatedText: String) {
        translate_from_lang.tag = sourceLang
        translate_from_lang.text =
            if (sourceLangAutoDetected)
                "${sourceLang} (${getString(R.string.auto_detected)})"
            else
                sourceLang.toString()

        translate_to_lang.tag = targetLang
        translate_to_lang.text = "${targetLang} (${targetLang.label()})"

        original_text.text = originalText
        translated_text.animateText(translatedText)

    }

    fun handleIntent(intent: Intent?) {
        intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.let { originalText ->
//            val toLang = appHelper.mostProbableSecondLanguage(Lang.EN)
            var toLang: Lang = appHelper.secondLang?.let { Lang.valueOf(it) } ?: Lang.EN

            translate_from_lang.text = appHelper.firstLang ?: ""
            translate_to_lang.text = toLang.toString()
            original_text.animateText(originalText)

            lifecycle.coroutineScope.launch {
                val (detectedSourceLang, translatedText) = translate(targetLang = toLang, text = originalText.toString())

                setData(true, detectedSourceLang, toLang, originalText.toString(), translatedText)
                appHelper.firstLang = detectedSourceLang.toString()
                appHelper.secondLang = toLang.toString()

                if (detectedSourceLang == toLang) { // if the lang of the original text matched the target lang, let's replace target with the other favorite language
                    toLang = appHelper.firstLang?.let { Lang.valueOf(it) } ?: Lang.EN
                    val (detectedLang, translated) = translate(targetLang = toLang, text = originalText.toString())

                    setData(true, detectedLang, toLang, originalText.toString(), translated)
                    appHelper.firstLang = detectedLang.toString()
                    appHelper.secondLang = toLang.toString()
                }
            }

        }
    }
}
