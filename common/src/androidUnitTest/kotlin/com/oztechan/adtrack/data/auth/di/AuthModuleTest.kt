/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.data.auth.di

import android.app.Application
import com.oztechan.adtrack.core.network.di.networkModule
import com.oztechan.adtrack.core.storage.SecureStorage
import com.oztechan.adtrack.data.auth.AuthService
import com.oztechan.adtrack.data.auth.token.TokenProvider
import com.oztechan.adtrack.di.testPlatformDeps
import com.oztechan.adtrack.domain.repository.AuthRepository
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
class AuthModuleTest {

    @AfterTest
    fun tearDown() = stopKoin()

    @Test
    fun provides_auth_dependencies() {
        val koin = startKoin {
            modules(authModule, networkModule, testPlatformDeps())
        }.koin

        assertNotNull(koin.getOrNull<SecureStorage>())
        assertNotNull(koin.getOrNull<AuthService>())
        assertNotNull(koin.getOrNull<TokenProvider>())
        assertNotNull(koin.getOrNull<AuthRepository>())
    }
}
