/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.data.auth.browser

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

class AndroidAuthBrowserLauncher(
    private val context: Context,
    private val redirectBus: AuthRedirectBus
) : AuthBrowserLauncher {

    override suspend fun authenticate(authUrl: String, callbackScheme: String): String {
        val deferred = redirectBus.start()
        CustomTabsIntent.Builder().build().apply {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            launchUrl(context, Uri.parse(authUrl))
        }
        return deferred.await()
    }
}
