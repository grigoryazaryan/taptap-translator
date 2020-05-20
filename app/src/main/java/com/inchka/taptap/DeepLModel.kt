package com.inchka.taptap

import com.google.gson.annotations.SerializedName

/**
 * Created by Grigory Azaryan on 5/18/20.
 */

data class DeepLRequest(
    @SerializedName("auth_key")
    val authKey: String,
    val text: String
)

data class DeepLResponse(
    val id: Int,
    val result: DeepLResult
)


data class ResponseUsage(val character_count: Int, val character_limit: Int)
data class ResponseTranslate(val translations: List<Translation>)

data class Translation(val detected_source_language: Lang, val text: String)

data class DeepLResult(
    @SerializedName("source_lang")
    val sourceLang: Lang,
    @SerializedName("target_lang")
    val targetLang: Lang,
    @SerializedName("translations")
    val translations: List<Translation>, // list of sentences
    @SerializedName("source_lang_is_confident")
    val sourceLangConfident: Int
)


data class Beam(
    @SerializedName("postprocessed_sentence")
    val sentence: String,
    @SerializedName("score")
    val score: Double
)

enum class Lang {
    EN, RU
}