package com.inchka.taptap.activity

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.inchka.taptap.R
import com.inchka.taptap.deepl.Lang
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Grigory Azaryan on 5/20/20.
 */
@RunWith(AndroidJUnit4::class)
class TranslateActivityTest {

    val intent: Intent = Intent(ApplicationProvider.getApplicationContext(), TranslateActivity::class.java).putExtra(Intent.EXTRA_PROCESS_TEXT, "Hello, world!")

//    @get: Rule
//    var activityScenarioRule = activityScenarioRule<TranslateActivity>()

    @Test
    fun setData() {
        ActivityScenario.launch(TranslateActivity::class.java)
            .onActivity { activity: TranslateActivity? ->
                activity?.handleIntent(intent)

                onView(withId(R.id.original_text))
                    .check(ViewAssertions.matches(withText("Hello, world!")))

                onView(withId(R.id.translate_from_lang))
                    .check(ViewAssertions.matches(withText(Lang.EN.toString())))
            }
    }
}