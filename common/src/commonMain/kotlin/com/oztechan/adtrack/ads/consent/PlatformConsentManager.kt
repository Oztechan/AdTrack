/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.consent

/**
 * The platform's consent + ads capabilities, implemented per platform by a
 * `PlatformConsentManagerImpl` (Kotlin on Android, Swift on iOS). Both calls are synchronous, so the
 * platform boundary carries no callbacks or lambdas — the async UMP sequencing stays in the platform
 * impl, and the shared [ConsentManager] only makes the go/no-go decision.
 */
interface PlatformConsentManager {
    /** Whether ads may be requested with the consent gathered so far. */
    fun canRequestAds(): Boolean

    /** Initialize the ad SDK. */
    fun initializeAds()
}
