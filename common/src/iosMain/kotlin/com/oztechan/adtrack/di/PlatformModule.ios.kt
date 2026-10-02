/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.di

import com.oztechan.adtrack.core.storage.PreferenceStorage
import com.oztechan.adtrack.data.auth.browser.AuthBrowserLauncher
import com.oztechan.adtrack.data.auth.browser.IosAuthBrowserLauncher
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

private const val KEYCHAIN_SERVICE = "com.oztechan.adtrack"

@OptIn(ExperimentalSettingsImplementation::class)
actual val platformModule: Module = module {
    // Secure, persists across uninstall (Keychain) — auth tokens.
    single<Settings> { KeychainSettings(service = KEYCHAIN_SERVICE) }
    // Sandbox-scoped, wiped on uninstall (NSUserDefaults) — premium window.
    single { PreferenceStorage(NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)) }
    single<AuthBrowserLauncher> { IosAuthBrowserLauncher() }
}
