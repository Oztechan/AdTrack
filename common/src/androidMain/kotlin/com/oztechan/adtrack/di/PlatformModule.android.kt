/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.di

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.oztechan.adtrack.core.storage.PreferenceStorage
import com.oztechan.adtrack.data.auth.browser.AndroidAuthBrowserLauncher
import com.oztechan.adtrack.data.auth.browser.AuthBrowserLauncher
import com.oztechan.adtrack.data.auth.browser.AuthRedirectBus
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

private const val SECURE_PREFS_NAME = "adtrack_secure_prefs"

actual val platformModule: Module = module {
    // Secure store (EncryptedSharedPreferences) — auth tokens. Wiped on uninstall like all app data.
    single<Settings> {
        val context = androidContext()
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        val prefs = EncryptedSharedPreferences.create(
            context,
            SECURE_PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        SharedPreferencesSettings(prefs)
    }

    // Premium window. Android wipes all app storage on uninstall anyway, so premium already resets on
    // reinstall here — this just reuses the existing store. (The iOS side is what needed changing.)
    single { PreferenceStorage(get()) }

    single { AuthRedirectBus() }
    single<AuthBrowserLauncher> { AndroidAuthBrowserLauncher(androidContext(), get()) }
}
