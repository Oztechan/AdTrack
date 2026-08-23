/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.di

import com.oztechan.adtrack.data.auth.browser.AuthBrowserLauncher
import com.oztechan.adtrack.data.auth.token.TokenProvider
import com.oztechan.adtrack.fakes.FakeAuthBrowserLauncher
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Stand-in for the platform module in Koin wiring tests: supplies the platform-provided singletons
 * (secure [Settings] store + [AuthBrowserLauncher]) the common modules depend on, without needing a
 * real device/Context. The production `platformModule` (Android) is verified separately.
 */
internal fun testPlatformDeps(): Module = module {
    single<Settings> { MapSettings() }
    single<AuthBrowserLauncher> { FakeAuthBrowserLauncher() }
}

/** Minimal [TokenProvider] so the authenticated Ktor client can be built without the auth graph. */
internal class FakeTokenProvider : TokenProvider {
    override suspend fun validAccessToken(): String? = null
    override suspend fun refreshAccessToken(): String? = null
}
