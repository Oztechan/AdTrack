/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.premium

import com.oztechan.adtrack.core.storage.SecureStorage
import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

private class FakeClock(var epochSeconds: Long) : Clock {
    override fun now(): Instant = Instant.fromEpochSeconds(epochSeconds)
    fun advance(seconds: Long) {
        epochSeconds += seconds
    }
}

class PremiumManagerImplTest {

    private val settings = MapSettings()
    private val clock = FakeClock(epochSeconds = 1_000_000)
    private fun manager() = PremiumManagerImpl(SecureStorage(settings), clock)

    @Test
    fun not_premium_by_default() {
        assertFalse(manager().isPremium.value)
    }

    @Test
    fun grant_makes_premium() {
        val premium = manager()
        premium.grantPremium(2.days)
        assertTrue(premium.isPremium.value)
    }

    @Test
    fun premium_expires_after_the_window() {
        val premium = manager()
        premium.grantPremium(1.days)
        assertTrue(premium.isPremium.value)

        clock.advance(2.days.inWholeSeconds)
        premium.refresh()
        assertFalse(premium.isPremium.value)
    }

    @Test
    fun grants_stack_on_the_existing_window() {
        val premium = manager()
        premium.grantPremium(1.days)
        clock.advance(12.hours.inWholeSeconds) // 12h left on the first grant
        premium.grantPremium(1.days) // extends from the existing expiry, not from now

        // 12h remaining + 1 day = 36h left; still premium 30h from now.
        clock.advance(30.hours.inWholeSeconds)
        premium.refresh()
        assertTrue(premium.isPremium.value)

        clock.advance(12.hours.inWholeSeconds) // now past the stacked expiry
        premium.refresh()
        assertFalse(premium.isPremium.value)
    }

    @Test
    fun state_persists_across_instances() {
        manager().grantPremium(2.days)

        // A fresh manager over the same storage sees the active window.
        assertTrue(manager().isPremium.value)
    }

    @Test
    fun expired_window_reads_as_not_premium_on_a_new_instance() {
        manager().grantPremium(1.days)
        clock.advance(2.days.inWholeSeconds)

        assertEquals(false, manager().isPremium.value)
    }
}
