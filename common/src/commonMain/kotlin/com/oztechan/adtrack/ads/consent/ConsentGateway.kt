/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.consent

/**
 * Platform-agnostic seam over a consent SDK (Android/iOS UMP). Lets [ConsentCoordinator] drive the
 * flow without depending on any platform SDK — the shape a future shared ConsentMob library takes.
 */
interface ConsentGateway {
    /** Whether ads may be requested with the consent gathered so far. */
    fun canRequestAds(): Boolean

    /** Refresh consent status; [onComplete] is called on success or failure. */
    fun requestConsentInfoUpdate(onComplete: () -> Unit)

    /** Show the consent form only if the user is required to see it; [onComplete] when done. */
    fun loadAndShowFormIfRequired(onComplete: () -> Unit)
}
