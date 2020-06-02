package com.inchka.translator.model

/**
 * Created by Grigory Azaryan on 5/21/20.
 */


enum class Lang {
    EN, ES, PT, FR, DE, IT, RU, NL, PL, JA, ZH
}

fun Lang.label() = when (this) {
    Lang.EN -> "English"
    Lang.ES -> "Spanish"
    Lang.PT -> "Portuguese"
    Lang.FR -> "French"
    Lang.DE -> "German"
    Lang.IT -> "Italian"
    Lang.RU -> "Russian"
    Lang.NL -> "Dutch"
    Lang.PL -> "Polish"
    Lang.JA -> "Japanese"
    Lang.ZH -> "Chinese"
}


data class TranslateResult(val detectedLang: Lang, val translatedText: String)