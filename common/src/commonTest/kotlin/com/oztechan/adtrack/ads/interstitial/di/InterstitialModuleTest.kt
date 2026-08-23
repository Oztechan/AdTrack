/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.interstitial.di

import com.oztechan.adtrack.ads.interstitial.InterstitialAdConfig
import com.oztechan.adtrack.ads.interstitial.InterstitialManager
import com.oztechan.adtrack.ads.interstitial.PlatformInterstitialAd
import com.oztechan.adtrack.ads.premium.PremiumManager
import com.oztechan.adtrack.ads.premium.PremiumManagerImpl
import com.oztechan.adtrack.core.storage.SecureStorage
import com.russhwolf.settings.MapSettings
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private class NoopPlatformInterstitialAd : PlatformInterstitialAd {
    override fun load() = Unit
    override fun isReady(): Boolean = false
    override fun show() = Unit
}

class InterstitialModuleTest {

    @AfterTest
    fun tearDown() = stopKoin()

    @Test
    fun provides_interstitial_dependencies() {
        val koin = startKoin {
            modules(
                module {
                    single<PremiumManager> { PremiumManagerImpl(SecureStorage(MapSettings())) }
                    single<PlatformInterstitialAd> { NoopPlatformInterstitialAd() }
                },
                interstitialModule
            )
        }.koin

        assertNotNull(koin.getOrNull<InterstitialManager>())
        assertTrue(koin.get<InterstitialAdConfig>().adUnitId.isNotBlank())
    }
}
