/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.premium.di

import android.app.Application
import com.oztechan.adtrack.ads.premium.PremiumManager
import com.oztechan.adtrack.core.storage.SecureStorage
import com.oztechan.adtrack.di.testPlatformDeps
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class PremiumModuleTest {

    @AfterTest
    fun tearDown() = stopKoin()

    @Test
    fun provides_premium_manager() {
        val koin = startKoin {
            modules(
                premiumModule,
                testPlatformDeps(),
                module { single { SecureStorage(get()) } }
            )
        }.koin

        assertNotNull(koin.getOrNull<PremiumManager>())
    }
}
