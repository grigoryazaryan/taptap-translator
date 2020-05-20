package com.inchka.taptap.helpers

/**
 * Created by Grigory Azaryan on 5/18/20.
 */

class Constants {
    companion object{

        val DEEPL_TOK = "1bbf370c-7d8d-229e-ee91-31a1c58cc9df"

        val SOURCE_LANG = "source_lang"
        val TARGET_LANG = "target_lang"


        val ADS_TYPE = "ads_type"

        val remoteConfigDefaults = mapOf<String, Any>(ADS_TYPE to 1)
    }
}