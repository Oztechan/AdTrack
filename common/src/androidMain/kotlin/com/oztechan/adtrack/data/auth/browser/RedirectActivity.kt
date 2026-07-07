/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.data.auth.browser

import android.os.Bundle
import androidx.activity.ComponentActivity
import org.koin.android.ext.android.inject

/**
 * Receives the OAuth redirect deep link from Custom Tabs, forwards it to [AuthRedirectBus], and
 * finishes immediately (renders nothing). Registered in the manifest with a custom-scheme
 * intent-filter and `singleTask` launch mode.
 */
class RedirectActivity : ComponentActivity() {

    private val redirectBus: AuthRedirectBus by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.data?.let { redirectBus.onRedirect(it.toString()) }
        finish()
    }
}
