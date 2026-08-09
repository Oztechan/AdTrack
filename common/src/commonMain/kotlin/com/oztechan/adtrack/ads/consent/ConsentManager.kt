/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.consent

/** Shared, platform-agnostic consent manager the app drives at launch. */
interface ConsentManager {
    /** Gather consent, then initialize ads once (and only once) they may be requested. */
    fun gatherConsentThenInitializeAds()
}
