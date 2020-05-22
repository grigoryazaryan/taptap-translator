package com.inchka.taptap.deepl

import com.inchka.taptap.model.Lang

/**
 * Created by Grigory Azaryan on 5/18/20.
 */


data class ResponseUsage(val character_count: Int, val character_limit: Int)
data class ResponseTranslate(val translations: List<Translation>)

data class Translation(val detected_source_language: Lang, val text: String)
