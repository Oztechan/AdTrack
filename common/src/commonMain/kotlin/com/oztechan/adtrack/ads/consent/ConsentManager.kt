/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.consent

/** Shared, platform-agnostic decision: initialize ads once consent permits, at most once. */
interface ConsentManager {
    /**
     * Initialize ads via [PlatformConsentManager] if consent now permits it, guaranteeing a single
     * initialization. Safe to call repeatedly — after the consent update, after the form, and once
     * immediately for returning users — since only the first permitted call takes effect.
     */
    fun initializeAdsIfPermitted()
}
