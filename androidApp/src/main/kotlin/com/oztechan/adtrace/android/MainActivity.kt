/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.oztechan.adtrace.AdTraceApp
import com.oztechan.adtrace.data.auth.browser.AuthRedirectBus
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val redirectBus: AuthRedirectBus by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must be called before super.onCreate; hands the splash off to the app's content.
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AdTraceApp()
        }
    }

    override fun onResume() {
        super.onResume()
        // If we returned without an OAuth redirect arriving, the user cancelled in the Custom Tab.
        redirectBus.reconcileOnResume()
    }
}
