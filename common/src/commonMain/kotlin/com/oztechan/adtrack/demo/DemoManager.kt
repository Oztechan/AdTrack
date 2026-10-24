/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.demo

/**
 * Session-scoped switch for demo mode: lets someone explore the app with sample data instead of
 * signing in with Google. Not persisted — demo lasts only for the current launch, so a real sign-in
 * is unaffected and the app always starts on the sign-in screen for a brand-new user. Primarily this
 * exists so app-store reviewers can see the app without a Google/AdMob account.
 */
interface DemoManager {
    val isDemo: Boolean
    fun enterDemo()
    fun exitDemo()
}
