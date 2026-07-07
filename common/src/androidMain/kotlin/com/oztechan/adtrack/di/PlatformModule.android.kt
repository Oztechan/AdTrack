/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.di

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
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

    single { AuthRedirectBus() }
    single<AuthBrowserLauncher> { AndroidAuthBrowserLauncher(androidContext(), get()) }
}
