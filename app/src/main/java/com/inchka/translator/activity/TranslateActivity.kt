package com.inchka.translator.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import com.google.android.gms.ads.AdRequest
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.inchka.translator.App
import com.inchka.translator.BuildConfig
import com.inchka.translator.R
import com.inchka.translator.helpers.AppHelper
import com.inchka.translator.helpers.Constants
import com.inchka.translator.model.Lang
import com.inchka.translator.model.label
import com.inchka.translator.viewmodel.TranslationRepository
import kotlinx.android.synthetic.main.activity_translate.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


class TranslateActivity : AppCompatActivity() {

    @Inject
    lateinit var translationRepository: TranslationRepository

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

        button.isEnabled = false

        translate_from_lang.setOnClickListener {
            val fromLang = translate_from_lang.tag as? Lang
            val toLang = translate_to_lang.tag as? Lang ?: return@setOnClickListener

            showLangPickerDialog(fromLang) { langSelected ->
                val originalText = original_text.text.toString()
                lifecycle.coroutineScope.launch {
                    val (detectedLang, translatedText) = translationRepository.translate(targetLang = toLang, text = originalText, fromLang = langSelected)
                    setData(false, detectedLang, toLang, originalText, translatedText)
                    appHelper.firstLang = detectedLang.toString()
                    appHelper.secondLang = toLang.toString()
                }
            }
        }

        translate_to_lang.setOnClickListener {
            val lang = translate_to_lang.tag as? Lang
            showLangPickerDialog(lang) { langSelected ->
                val originalText = original_text.text.toString()
                lifecycle.coroutineScope.launch {
                    val (detectedLang, translatedText) = translationRepository.translate(langSelected, originalText)
                    setData(true, detectedLang, langSelected, originalText, translatedText)
                    appHelper.firstLang = detectedLang.toString()
                    appHelper.secondLang = langSelected.toString()
                }
            }
        }
//        intent.putExtra(Intent.EXTRA_PROCESS_TEXT, "Hello, world!")
//        intent.putExtra(Intent.EXTRA_PROCESS_TEXT, "привет, мир!")

        handleIntent(intent)

        loadAd()
    }

    private fun loadAd() {
        // check out Remote Config
        val shouldShowAds = remoteConfig.getBoolean(Constants.SHOW_ADS_IN_TRANSLATE_POPUP)
        Timber.v("shouldShowAds ${shouldShowAds}")

        val isPaidVersion = BuildConfig.APPLICATION_ID.endsWith(".paid") // todo remove in the near future

        if (!isPaidVersion && shouldShowAds) {
            val adRequest = AdRequest.Builder().build()
            adViewBanner.loadAd(adRequest)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    fun setData(sourceLangAutoDetected: Boolean, sourceLang: Lang, targetLang: Lang, originalText: String, translatedText: String) {
        translate_from_lang.tag = sourceLang
        translate_from_lang.text =
            if (sourceLangAutoDetected)
                "${sourceLang} (${getString(R.string.detected)})"
            else
                sourceLang.toString()

        translate_to_lang.tag = targetLang
        translate_to_lang.text = "${targetLang} (${targetLang.label()})"

        original_text.text = originalText
        translated_text.animateText(translatedText)

    }

    fun handleIntent(intent: Intent?) {
        intent?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
            ?.toString()
            ?.let { originalText ->

                lifecycle.coroutineScope.launch {
                    val firstLang: Lang? = appHelper.firstLang?.let { Lang.valueOf(it) }
                    val secondLang: Lang? = appHelper.secondLang?.let { Lang.valueOf(it) }

                    translate_from_lang.text = appHelper.firstLang ?: ""
                    translate_to_lang.text = secondLang.toString()
                    original_text.animateText(originalText)

                    if (firstLang == null && secondLang == null) { // initial state. no languages saved yet
                        val (detectedSourceLang, translatedText) = translationRepository.translate(targetLang = Lang.EN, text = originalText)

                        setData(true, detectedSourceLang, Lang.EN, originalText, translatedText)

                        appHelper.firstLang = detectedSourceLang.toString()
                        appHelper.secondLang = Lang.EN.toString()

                    } else { // at least one lang is known
                        var targetLang = secondLang ?: firstLang ?: Lang.EN
                        var (detectedSourceLang, translatedText) = translationRepository.translate(targetLang = targetLang, text = originalText)

                        if (detectedSourceLang == targetLang && targetLang != firstLang && firstLang != null) { // if the lang of the original text matched the target lang, let's replace target with the other favorite language
                            targetLang = firstLang

                            val (dtSourceLang, tText) = translationRepository.translate(targetLang = targetLang, text = originalText)
                            detectedSourceLang = dtSourceLang
                            translatedText = tText
                        }

                        setData(true, detectedSourceLang, targetLang, originalText, translatedText)

                        appHelper.firstLang = detectedSourceLang.toString()
                        appHelper.secondLang = targetLang.toString()
                    }
                }

            }
    }

}
