/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.consent

/**
 * Platform seam over a consent SDK (Android/iOS UMP), implemented per platform:
 * - Android: a Kotlin class using the UMP Android SDK.
 * - iOS: a Swift class conforming to this (exported) interface, using the UMP iOS SDK.
 *
 * [ConsentCoordinator] drives both through this contract, so the consent logic is shared and
 * platform-agnostic — the shape a shared ConsentMob library takes across apps.
 */
interface ConsentGateway {
    /** Whether ads may be requested with the consent gathered so far. */
    fun canRequestAds(): Boolean

    /** Refresh consent status; [onComplete] is called on success or failure. */
    fun requestConsentInfoUpdate(onComplete: () -> Unit)

    /** Show the consent form only if the user is required to see it; [onComplete] when done. */
    fun loadAndShowFormIfRequired(onComplete: () -> Unit)
}
