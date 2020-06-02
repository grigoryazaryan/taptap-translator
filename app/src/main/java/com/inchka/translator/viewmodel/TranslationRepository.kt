package com.inchka.translator.viewmodel

import com.inchka.translator.App
import com.inchka.translator.deepl.DeepL
import com.inchka.translator.deepl.Translation
import com.inchka.translator.model.Lang
import com.inchka.translator.model.TranslateResult
import javax.inject.Inject

class TranslationRepository @Inject constructor(val deepL: DeepL) {


        suspend fun translate(targetLang: Lang, text: String, fromLang: Lang? = null): TranslateResult {
            val sourceLang = fromLang?.let { it.toString() } ?: ""
            val resp = deepL.translate(text = text, target_lang = targetLang, source_lang = sourceLang)
            val translation = resp.takeIf { it.translations.isNotEmpty() }
                ?.translations
                ?.first() ?: Translation(Lang.EN, "")

            return TranslateResult(translation.detected_source_language, translation.text)
        }
}