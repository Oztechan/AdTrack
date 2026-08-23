/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.android.ads

import android.app.Activity
import android.app.Application
import com.oztechan.adtrack.ads.interstitial.InterstitialAdConfig
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertFalse

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class AndroidInterstitialAdTest {

    private val context: Application = Robolectric.buildActivity(Activity::class.java).get().application
    private val activityHolder = CurrentActivityHolder()

    private fun interstitial() = AndroidInterstitialAd(
        context = context,
        activityHolder = activityHolder,
        config = InterstitialAdConfig("ca-app-pub-test/interstitial")
    )

    @Test
    fun is_not_ready_before_anything_loads() {
        assertFalse(interstitial().isReady())
    }

    @Test
    fun load_then_show_without_a_loaded_ad_is_harmless() {
        val ad = interstitial()
        ad.load() // async; the SDK never calls back under Robolectric, so nothing becomes ready
        ad.show() // no loaded ad + no resumed activity -> no-op, must not throw
        assertFalse(ad.isReady())
    }
}
