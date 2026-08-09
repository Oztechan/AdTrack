/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.consent

import kotlin.test.Test
import kotlin.test.assertEquals

class ConsentManagerImplTest {

    private class FakePlatformConsentManager(var canRequest: Boolean = true) : PlatformConsentManager {
        var updateCalls = 0
        var formCalls = 0
        var initAdsCalls = 0
        private var pendingUpdate: ConsentCallback? = null
        private var pendingForm: ConsentCallback? = null

        override fun canRequestAds() = canRequest

        override fun requestConsentInfoUpdate(callback: ConsentCallback) {
            updateCalls++
            pendingUpdate = callback
        }

        override fun loadAndShowFormIfRequired(callback: ConsentCallback) {
            formCalls++
            pendingForm = callback
        }

        override fun initializeAds() {
            initAdsCalls++
        }

        fun completeUpdate() = pendingUpdate?.onCompleted()
        fun completeForm() = pendingForm?.onCompleted()
    }

    @Test
    fun ads_init_immediately_for_returning_users_who_can_already_request() {
        val platform = FakePlatformConsentManager(canRequest = true)
        ConsentManagerImpl(platform).gatherConsentThenInitializeAds()

        // The immediate path fires before any consent callback resolves.
        assertEquals(1, platform.initAdsCalls)
    }

    @Test
    fun ads_do_not_init_until_consent_permits() {
        val platform = FakePlatformConsentManager(canRequest = false)
        ConsentManagerImpl(platform).gatherConsentThenInitializeAds()
        assertEquals(0, platform.initAdsCalls)

        // Once the form flow completes and consent allows ads, they initialize.
        platform.canRequest = true
        platform.completeUpdate()
        platform.completeForm()
        assertEquals(1, platform.initAdsCalls)
    }

    @Test
    fun ads_init_at_most_once_across_immediate_and_callback_paths() {
        val platform = FakePlatformConsentManager(canRequest = true)
        ConsentManagerImpl(platform).gatherConsentThenInitializeAds()
        platform.completeUpdate()
        platform.completeForm()

        // Immediate + callback paths both reach the guard, but ads only ever init once.
        assertEquals(1, platform.initAdsCalls)
    }

    @Test
    fun flow_requests_update_then_shows_form() {
        val platform = FakePlatformConsentManager(canRequest = false)
        ConsentManagerImpl(platform).gatherConsentThenInitializeAds()
        assertEquals(1, platform.updateCalls)

        platform.completeUpdate()
        assertEquals(1, platform.formCalls)
    }
}
