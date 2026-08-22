/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.banner

import android.content.Context
import android.view.View
import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.oztechan.adtrack.ads.premium.PremiumManager
import com.oztechan.adtrack.ads.premium.PremiumManagerImpl
import com.oztechan.adtrack.core.storage.SecureStorage
import com.russhwolf.settings.MapSettings
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days

/**
 * Verifies the one shared decision the banner makes: [BannerAd] renders the native [PlatformBanner]
 * only while the user is not premium. The banner view is faked so no real ad SDK is exercised.
 */
@RunWith(RobolectricTestRunner::class)
class BannerAdTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val premiumManager: PremiumManager = PremiumManagerImpl(SecureStorage(MapSettings()))
    private var bannerCreations = 0

    private fun startKoinWith() = startKoin {
        modules(
            module {
                single { premiumManager }
                single<AndroidBannerFactory> {
                    object : AndroidBannerFactory {
                        override fun create(context: Context, adUnitId: String): View {
                            bannerCreations++
                            return View(context)
                        }
                    }
                }
            }
        )
    }

    @After
    fun tearDown() = stopKoin()

    @Test
    fun shows_banner_when_not_premium() {
        startKoinWith()

        composeRule.setContent { BannerAd() }
        composeRule.waitForIdle()

        assertEquals(1, bannerCreations)
    }

    @Test
    fun hides_banner_when_premium() {
        premiumManager.grantPremium(2.days)
        startKoinWith()

        composeRule.setContent { BannerAd() }
        composeRule.waitForIdle()

        assertEquals(0, bannerCreations)
    }
}
