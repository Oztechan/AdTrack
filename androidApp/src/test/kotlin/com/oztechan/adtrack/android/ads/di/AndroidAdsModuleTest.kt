/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.android.ads.di

import com.oztechan.adtrack.ads.banner.AndroidBannerFactory
import org.junit.After
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import kotlin.test.assertNotNull

class AndroidAdsModuleTest {

    @After
    fun tearDown() = stopKoin()

    @Test
    fun provides_the_android_banner_factory() {
        val koin = startKoin { modules(androidAdsModule) }.koin

        assertNotNull(koin.getOrNull<AndroidBannerFactory>())
    }
}
