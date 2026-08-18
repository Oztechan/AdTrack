/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.android.ads

import android.app.Application
import com.google.android.gms.ads.AdView
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

// Use a plain Application so Robolectric doesn't boot AdTrackApplication (which needs Firebase).
@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class AndroidBannerFactoryImplTest {

    private val context = RuntimeEnvironment.getApplication()

    @Test
    fun builds_an_adview_with_the_given_unit_id_and_a_size() {
        val view = AndroidBannerFactoryImpl().create(context, "ca-app-pub-test/123")

        val adView = assertNotNull(view as? AdView)
        assertEquals("ca-app-pub-test/123", adView.adUnitId)
        // An anchored adaptive size was applied (non-zero width for the current screen).
        assertTrue(requireNotNull(adView.adSize).width > 0)
    }
}
