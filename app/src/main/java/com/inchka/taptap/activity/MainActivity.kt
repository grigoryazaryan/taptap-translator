package com.inchka.taptap.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.inchka.taptap.App
import com.inchka.taptap.DeepL
import com.inchka.taptap.R
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    @Inject
    lateinit var deepL: DeepL

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
