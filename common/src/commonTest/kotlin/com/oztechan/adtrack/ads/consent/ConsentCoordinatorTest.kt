/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.consent

import kotlin.test.Test
import kotlin.test.assertEquals

class ConsentCoordinatorTest {

    private class FakeConsentGateway(var canRequest: Boolean = true) : ConsentGateway {
        var updateCalls = 0
        var formCalls = 0
        private var pendingUpdate: (() -> Unit)? = null
        private var pendingForm: (() -> Unit)? = null

        override fun canRequestAds() = canRequest

        override fun requestConsentInfoUpdate(onComplete: () -> Unit) {
            updateCalls++
            pendingUpdate = onComplete
        }

        override fun loadAndShowFormIfRequired(onComplete: () -> Unit) {
            formCalls++
            pendingForm = onComplete
        }

        fun completeUpdate() = pendingUpdate?.invoke()
        fun completeForm() = pendingForm?.invoke()
    }

    @Test
    fun ads_start_immediately_for_returning_users_who_can_already_request() {
        val gateway = FakeConsentGateway(canRequest = true)
        var readyCount = 0
        ConsentCoordinator(gateway) { readyCount++ }.gatherConsentThenInitializeAds()

        // The immediate path fires before any consent callback resolves.
        assertEquals(1, readyCount)
    }

    @Test
    fun ads_do_not_start_until_consent_permits() {
        val gateway = FakeConsentGateway(canRequest = false)
        var readyCount = 0
        ConsentCoordinator(gateway) { readyCount++ }.gatherConsentThenInitializeAds()
        assertEquals(0, readyCount)

        // Once the form flow completes and consent allows ads, they start.
        gateway.canRequest = true
        gateway.completeUpdate()
        gateway.completeForm()
        assertEquals(1, readyCount)
    }

    @Test
    fun ads_start_at_most_once_across_immediate_and_callback_paths() {
        val gateway = FakeConsentGateway(canRequest = true)
        var readyCount = 0
        ConsentCoordinator(gateway) { readyCount++ }.gatherConsentThenInitializeAds()
        gateway.completeUpdate()
        gateway.completeForm()

        // Immediate + callback paths both reach the guard, but ads only ever start once.
        assertEquals(1, readyCount)
    }

    @Test
    fun consent_flow_requests_update_then_shows_form() {
        val gateway = FakeConsentGateway(canRequest = false)
        ConsentCoordinator(gateway) {}.gatherConsentThenInitializeAds()
        assertEquals(1, gateway.updateCalls)

        gateway.completeUpdate()
        assertEquals(1, gateway.formCalls)
    }
}
