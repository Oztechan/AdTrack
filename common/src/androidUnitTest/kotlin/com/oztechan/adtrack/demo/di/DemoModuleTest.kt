/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.demo.di

import android.app.Application
import com.oztechan.adtrack.core.network.di.networkModule
import com.oztechan.adtrack.data.admob.di.adMobModule
import com.oztechan.adtrack.data.auth.di.authModule
import com.oztechan.adtrack.demo.DemoManager
import com.oztechan.adtrack.di.testPlatformDeps
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
class DemoModuleTest {

    @AfterTest
    fun tearDown() = stopKoin()

    @Test
    fun provides_demo_dependencies() {
        val koin = startKoin {
            modules(demoModule, adMobModule, authModule, networkModule, testPlatformDeps())
        }.koin

        assertNotNull(koin.getOrNull<DemoManager>())
        // The app-wide RevenueRepository binding is the demo overlay provided here.
        assertNotNull(koin.getOrNull<RevenueRepository>())
    }
}
