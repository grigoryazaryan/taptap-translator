package com.inchka.translator.activity

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.coroutineScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.inchka.translator.App
import com.inchka.translator.BuildConfig
import com.inchka.translator.R
import com.inchka.translator.helpers.AppHelper
import com.inchka.translator.helpers.Constants
import com.inchka.translator.helpers.InAppUpdateHelper
import com.inchka.translator.helpers.hideKeyboard
import com.inchka.translator.model.Lang
import com.inchka.translator.model.label
import com.inchka.translator.viewmodel.TranslationRepository
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var translationRepository: TranslationRepository

    @Inject
    lateinit var appHelper: AppHelper

    @Inject
    lateinit var remoteConfig: FirebaseRemoteConfig

    private var inAppUpdateHelper: InAppUpdateHelper = InAppUpdateHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)

        // just to notice that Remote Config works
        Timber.v("adstype ${remoteConfig.getLong(Constants.ADS_TYPE)}")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // make keyboard closable with OK button
        original_text.imeOptions = EditorInfo.IME_ACTION_GO
        original_text.setRawInputType(InputType.TYPE_CLASS_TEXT)

        copy.setOnClickListener {
            appHelper.copyToClipboard(translated_text.text.toString())
            appHelper.showToast(getString(R.string.copied_to_clipboard))
        }

        switch_languages.setOnClickListener {
            val from = (translate_from_lang.getTag(R.integer.lang) as? Lang)
            val to = (translate_to_lang.getTag(R.integer.lang) as? Lang)

            if (from != null && to != null) {
                translate_from_lang.setTag(R.integer.auto_detected, false) // remove label

                setLangSelector(translate_from_lang, to)
                setLangSelector(translate_to_lang, from)
            }
        }

        button.setOnClickListener {
            val originalText = original_text.text.toString()

            if (originalText.isBlank()) return@setOnClickListener

            val fromLangSetManually: Boolean = (translate_from_lang.getTag(R.integer.auto_detected) as? Boolean) ?: false

            val fromLang: Lang? = if (fromLangSetManually)
                translate_from_lang.getTag(R.integer.lang) as? Lang
            else null

            (translate_to_lang.getTag(R.integer.lang) as? Lang)?.let { toLang ->
                lifecycle.coroutineScope.launch {
                    //disable while making request not to double it
                    button.isEnabled = false

                    val (detectedLang, translatedText) = translationRepository.translate(toLang, originalText, fromLang)
                    // enable
                    button.isEnabled = true

//                    hideKeyboard()

                    setData(!fromLangSetManually, detectedLang, toLang, translatedText)

                    appHelper.firstLang = toLang.toString()
                    appHelper.secondLang = detectedLang.toString()
                }
            }
        }

        translate_from_lang.setOnClickListener {
            val fromLang = translate_from_lang.getTag(R.integer.lang) as? Lang
            showLangPickerDialog(fromLang) { langSelected ->
                setLangSelector(translate_from_lang, langSelected)
            }
        }

        translate_to_lang.setOnClickListener {
            val lang = translate_to_lang.getTag(R.integer.lang) as? Lang
            showLangPickerDialog(lang) { langSelected ->
                setLangSelector(translate_to_lang, langSelected)
            }
        }

        initViews()

        inAppUpdateHelper.addOnDownloadCompleteListener {
            // After the update is downloaded, show a notification
            // and request user confirmation to restart the app.
            popupSnackbarForCompleteUpdate()
        }

        // show intro
        if (!appHelper.introPlayed)
            showIntro()

    }

    private fun popupSnackbarForCompleteUpdate() {
        Snackbar.make(background, R.string.update_been_downloaded, Snackbar.LENGTH_INDEFINITE).apply {
            setAction("RESTART") { inAppUpdateHelper.completeUpdate() }
            show()
        }
    }

    fun setData(sourceLangAutoDetected: Boolean, sourceLang: Lang, targetLang: Lang, translatedText: String) {
        translate_from_lang.setTag(R.integer.lang, sourceLang)
        translate_from_lang.setTag(R.integer.auto_detected, sourceLangAutoDetected)
        translate_from_lang.text =
            if (sourceLangAutoDetected)
                "${sourceLang} (${getString(R.string.detected)})"
            else
                sourceLang.toString()

        setLangSelector(translate_to_lang, targetLang)

        translated_text.animateText(translatedText)
    }

    private fun setLangSelector(textView: TextView, lang: Lang) {
        val autoDetected: Boolean? = textView.getTag(R.integer.auto_detected) as? Boolean
        val label = if (autoDetected == true) "(${getString(R.string.detected)})" else "(${lang.label()})"
        textView.setTag(R.integer.lang, lang)
        textView.text = "${lang} ${label}"
    }

    private fun initViews() {
        // set initial languages
        translate_from_lang.text = getString(R.string.auto_detect)

        val firstLang: Lang = appHelper.firstLang?.let { Lang.valueOf(it) } ?: Lang.EN
        setLangSelector(translate_to_lang, firstLang)

        copy.visibility = View.INVISIBLE
        translated_text.addTextChangedListener { text -> copy.visibility = if (text.isNullOrBlank()) View.INVISIBLE else View.VISIBLE }

        button.visibility = View.INVISIBLE
        original_text.addTextChangedListener { text -> button.visibility = if (text.isNullOrBlank()) View.INVISIBLE else View.VISIBLE }

        original_text.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                button.callOnClick()
                hideKeyboard()
            }

            return@setOnEditorActionListener false
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        hideKeyboard()
        return super.onTouchEvent(event)
    }

    private fun showIntro() {
        val uri = Uri.parse("android.resource://${BuildConfig.APPLICATION_ID}/" + R.raw.intro)
        val videoView = VideoView(this)
        videoView.setVideoURI(uri)
        videoView.setOnPreparedListener { mp: MediaPlayer? -> mp?.isLooping = true }
        videoView.start()

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.did_you_know)
            .setMessage(R.string.you_can_use_text_menu)
            .setView(videoView)
            .setPositiveButton("OK") { _, _ -> appHelper.introPlayed = true }
            .show()
    }
}
