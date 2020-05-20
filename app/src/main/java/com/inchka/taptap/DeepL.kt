package com.inchka.taptap

import com.inchka.taptap.helpers.Constants
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by Grigory Azaryan on 5/18/20.
 */

interface DeepL {

    @POST("/v2/usage")
    suspend fun usage(@Query("auth_key") auth_key: String = Constants.DEEPL_TOK): ResponseUsage

    @POST("/v2/translate")
    suspend fun translate(
        @Query("auth_key") auth_key: String = Constants.DEEPL_TOK, @Query("text") text: String, @Query("source_lang") source_lang: String = "",
        @Query("target_lang") target_lang: Lang, @Query("preserve_formatting") preserve_formatting: Int = 0,
        @Query("split_sentences") split_sentences: String = "1"//"nonewlines"
    ): ResponseTranslate

}