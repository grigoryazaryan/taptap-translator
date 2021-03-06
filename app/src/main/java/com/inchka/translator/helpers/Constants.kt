package com.inchka.translator.helpers

/**
 * Created by Grigory Azaryan on 5/18/20.
 */

class Constants {
    companion object {

        val DEEPL_TOK = "1bbf370c-7d8d-229e-ee91-31a1c58cc9df"

        val FIRST_LANG = "first_lang"
        val SECOND_LANG = "second_lang"
        val COUNTRY = "country"
        val OS_LANG = "os_lang"

        val LANG = "lang"
        val AUTODETECTED = "autodetected"
        val INTRO_SEEN = "intro_seen"


        val SHOW_ADS_IN_TRANSLATE_POPUP = "show_ads_translate_popup"

        val remoteConfigDefaults = mapOf<String, Any>(SHOW_ADS_IN_TRANSLATE_POPUP to true)
    }
}