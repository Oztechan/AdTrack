/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.data.admob.di

import android.app.Application
import com.oztechan.adtrack.core.network.di.networkModule
import com.oztechan.adtrack.data.admob.api.AdMobApi
import com.oztechan.adtrack.data.auth.di.authModule
import com.oztechan.adtrack.di.testPlatformDeps
import com.oztechan.adtrack.domain.PeriodCalculator
import com.oztechan.adtrack.domain.repository.RevenueRepository
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class AdMobModuleTest {

    @AfterTest
    fun tearDown() = stopKoin()

    @Test
    fun provides_admob_dependencies() {
        val koin = startKoin {
            modules(adMobModule, networkModule, authModule, testPlatformDeps())
        }.koin

        assertNotNull(koin.getOrNull<AdMobApi>())
        assertNotNull(koin.getOrNull<PeriodCalculator>())
        assertNotNull(koin.getOrNull<RevenueRepository>())
    }
}
