package com.inchka.taptap.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.inchka.taptap.App
import com.inchka.taptap.DeepL
import com.inchka.taptap.R
import com.inchka.taptap.helpers.Constants
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    @Inject
    lateinit var deepL: DeepL

    @Inject
    lateinit var remoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.v(TAG, "adstype ${remoteConfig.getLong(Constants.ADS_TYPE)}")
    }
}
