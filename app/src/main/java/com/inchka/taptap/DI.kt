package com.inchka.taptap

import android.content.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.inchka.taptap.activity.MainActivity
import com.inchka.taptap.activity.TranslateActivity
import com.inchka.taptap.deepl.DeepL
import com.inchka.taptap.deepl.ResponseTranslate
import com.inchka.taptap.deepl.ResponseUsage
import com.inchka.taptap.deepl.Translation
import com.inchka.taptap.helpers.AppHelper
import com.inchka.taptap.helpers.Constants
import com.inchka.taptap.model.Lang
import dagger.Component
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Grigory Azaryan on 5/18/20.
 */
@Singleton
@Component(modules = [AppModule::class, FakeDeepLModule::class])
interface AppComponent {

    fun inject(obj: MainActivity)
    fun inject(obj: TranslateActivity)

}

@Module
class DeepLModule {
    @Provides
    @Singleton
    fun provideDeepL(): DeepL {

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.deepl.com")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DeepL::class.java)
    }
}

@Module
class AppModule @Inject constructor(private val context: Context) {

    @Provides
    @Singleton
    fun provideAppHelper(): AppHelper = AppHelper(context)

//    @Provides
//    @Singleton
//    fun provideDeepL(): DeepL {
//
//        val logging = HttpLoggingInterceptor()
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
//
//        val client: OkHttpClient = OkHttpClient.Builder()
//            .addInterceptor(logging)
//            .build()
//
//        return Retrofit.Builder()
//            .baseUrl("https://api.deepl.com")
//            .client(client)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(DeepL::class.java)
//    }

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig =
        Firebase.remoteConfig.apply {
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 1 else TimeUnit.HOURS.toSeconds(1)
            }

            setConfigSettingsAsync(configSettings)

            setDefaultsAsync(Constants.remoteConfigDefaults)

            fetchAndActivate()
        }
}

@Module
class FakeDeepLModule {

    @Provides
    @Singleton
    fun provideDeepL(): DeepL = object : DeepL {
        override suspend fun usage(auth_key: String): ResponseUsage = ResponseUsage(1, 1)

        override suspend fun translate(
            auth_key: String,
            text: String,
            source_lang: String,
            target_lang: Lang,
            preserve_formatting: Int,
            split_sentences: String
        ): ResponseTranslate =
//            ResponseTranslate(listOf(Translation(Lang.RU, "translated text from ru to EN FakeDeepLModule")))
            ResponseTranslate(listOf(Translation(Lang.EN, "¡Hola, mundo!")))
    }
}