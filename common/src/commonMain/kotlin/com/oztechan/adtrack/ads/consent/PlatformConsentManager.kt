/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.consent

/**
 * Platform consent operations (UMP + ads init), implemented per platform by a
 * `PlatformConsentManagerImpl` (Kotlin on Android, Swift on iOS). Async steps report back through
 * [ConsentCallback] rather than lambdas, so nothing captures a closure across the platform boundary.
 */
interface PlatformConsentManager {
    /** Whether ads may be requested with the consent gathered so far. */
    fun canRequestAds(): Boolean

    /** Refresh consent status; [callback] is notified on success or failure. */
    fun requestConsentInfoUpdate(callback: ConsentCallback)

    /** Show the consent form only if the user is required to see it; [callback] when done. */
    fun loadAndShowFormIfRequired(callback: ConsentCallback)

    /** Initialize the ad SDK. Called once, only when ads may be requested. */
    fun initializeAds()
}
