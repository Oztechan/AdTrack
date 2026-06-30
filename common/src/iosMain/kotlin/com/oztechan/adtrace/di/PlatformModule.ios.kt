/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.di

import com.oztechan.adtrace.data.auth.browser.AuthBrowserLauncher
import com.oztechan.adtrace.data.auth.browser.IosAuthBrowserLauncher
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module

private const val KEYCHAIN_SERVICE = "com.oztechan.adtrace"

@OptIn(ExperimentalSettingsImplementation::class)
actual val platformModule: Module = module {
    single<Settings> { KeychainSettings(service = KEYCHAIN_SERVICE) }
    single<AuthBrowserLauncher> { IosAuthBrowserLauncher() }
}
