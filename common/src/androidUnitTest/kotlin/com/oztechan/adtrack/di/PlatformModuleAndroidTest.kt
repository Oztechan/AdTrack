/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.di

import android.app.Application
import com.oztechan.adtrack.data.auth.browser.AuthBrowserLauncher
import com.oztechan.adtrack.data.auth.browser.AuthRedirectBus
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class PlatformModuleAndroidTest {

    @AfterTest
    fun tearDown() = stopKoin()

    @Test
    fun provides_android_platform_dependencies() {
        val koin = startKoin {
            androidContext(RuntimeEnvironment.getApplication())
            modules(platformModule)
        }.koin

        // The secure Settings store uses EncryptedSharedPreferences, which needs the AndroidKeyStore
        // (unavailable under Robolectric) — that binding is covered by instrumentation, not here.
        assertNotNull(koin.getOrNull<AuthRedirectBus>())
        assertNotNull(koin.getOrNull<AuthBrowserLauncher>())
    }
}
