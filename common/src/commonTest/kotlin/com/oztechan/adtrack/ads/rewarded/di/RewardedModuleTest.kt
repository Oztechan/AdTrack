/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.rewarded.di

import com.oztechan.adtrack.ads.premium.PremiumManager
import com.oztechan.adtrack.ads.premium.PremiumManagerImpl
import com.oztechan.adtrack.ads.rewarded.RewardedAdConfig
import com.oztechan.adtrack.ads.rewarded.RewardedAdManager
import com.oztechan.adtrack.core.storage.SecureStorage
import com.russhwolf.settings.MapSettings
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RewardedModuleTest {

    @AfterTest
    fun tearDown() = stopKoin()

    @Test
    fun provides_rewarded_dependencies() {
        val koin = startKoin {
            modules(
                module { single<PremiumManager> { PremiumManagerImpl(SecureStorage(MapSettings())) } },
                rewardedModule
            )
        }.koin

        assertNotNull(koin.getOrNull<RewardedAdManager>())
        assertTrue(koin.get<RewardedAdConfig>().adUnitId.isNotBlank())
    }
}
