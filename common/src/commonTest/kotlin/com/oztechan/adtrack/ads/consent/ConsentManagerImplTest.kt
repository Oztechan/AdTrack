/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.consent

import kotlin.test.Test
import kotlin.test.assertEquals

class ConsentManagerImplTest {

    private class FakePlatformConsentManager(var canRequest: Boolean = true) : PlatformConsentManager {
        var initAdsCalls = 0
        override fun canRequestAds() = canRequest
        override fun initializeAds() {
            initAdsCalls++
        }
    }

    @Test
    fun does_not_initialize_when_consent_does_not_permit() {
        val platform = FakePlatformConsentManager(canRequest = false)
        ConsentManagerImpl(platform).initializeAdsIfPermitted()
        assertEquals(0, platform.initAdsCalls)
    }

    @Test
    fun initializes_when_consent_permits() {
        val platform = FakePlatformConsentManager(canRequest = true)
        ConsentManagerImpl(platform).initializeAdsIfPermitted()
        assertEquals(1, platform.initAdsCalls)
    }

    @Test
    fun initializes_at_most_once_across_repeated_calls() {
        val platform = FakePlatformConsentManager(canRequest = true)
        val manager = ConsentManagerImpl(platform)
        manager.initializeAdsIfPermitted()
        manager.initializeAdsIfPermitted()
        manager.initializeAdsIfPermitted()
        assertEquals(1, platform.initAdsCalls)
    }

    @Test
    fun initializes_once_consent_becomes_available() {
        val platform = FakePlatformConsentManager(canRequest = false)
        val manager = ConsentManagerImpl(platform)
        manager.initializeAdsIfPermitted()
        assertEquals(0, platform.initAdsCalls)

        // A later call, after the form flow granted consent, initializes exactly once.
        platform.canRequest = true
        manager.initializeAdsIfPermitted()
        manager.initializeAdsIfPermitted()
        assertEquals(1, platform.initAdsCalls)
    }
}
