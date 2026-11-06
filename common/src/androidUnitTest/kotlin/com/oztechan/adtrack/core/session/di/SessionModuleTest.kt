/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.core.session.di

import android.app.Application
import com.oztechan.adtrack.core.session.SessionManager
import com.oztechan.adtrack.di.testPlatformDeps
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
class SessionModuleTest {

    @AfterTest
    fun tearDown() = stopKoin()

    @Test
    fun provides_session_manager() {
        val koin = startKoin {
            modules(
                sessionModule,
                testPlatformDeps()
            )
        }.koin

        assertNotNull(koin.getOrNull<SessionManager>())
    }
}
