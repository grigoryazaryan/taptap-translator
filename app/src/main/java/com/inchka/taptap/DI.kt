package com.inchka.taptap

import android.content.Context
import com.inchka.taptap.activity.MainActivity
import com.inchka.taptap.activity.TranslateActivity
import com.inchka.taptap.helpers.AppHelper
import dagger.Component
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Grigory Azaryan on 5/18/20.
 */
@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(obj: MainActivity)
    fun inject(obj: TranslateActivity)

}

@Module
class AppModule @Inject constructor(private val context: Context) {

    @Provides
    @Singleton
    fun provideAppHelper(): AppHelper = AppHelper(context)

    @Provides
    @Singleton
    fun provideDeepL(): DeepL {
        val headerInterceptor = object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val originalRequest: Request = chain.request()
                val token = ""

//                if (originalRequest.url.encodedPath == "/user") {
//                    return chain.proceed(originalRequest);
//                }
                val compressedRequest: Request = originalRequest.newBuilder()
                    .header("Cookie", "token=$token")
                    .build()

                return chain.proceed(compressedRequest)
            }
        }

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client: OkHttpClient = OkHttpClient.Builder()
//            .addInterceptor(headerInterceptor)
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