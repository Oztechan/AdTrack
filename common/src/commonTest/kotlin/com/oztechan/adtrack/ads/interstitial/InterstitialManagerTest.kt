/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.interstitial

import com.oztechan.adtrack.ads.premium.PremiumManager
import com.oztechan.adtrack.core.session.SessionManager
import com.oztechan.adtrack.demo.DemoManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Instant

private class FakePremiumManager(premium: Boolean) : PremiumManager {
    override val isPremium: StateFlow<Boolean> = MutableStateFlow(premium)
    override fun grantPremium(duration: Duration) = Unit
    override fun refresh() = Unit
}

private class FakeDemoManager(demo: Boolean) : DemoManager {
    override var isDemo: Boolean = demo
        private set

    override fun enterDemo() {
        isDemo = true
    }

    override fun exitDemo() {
        isDemo = false
    }
}

private class FakeSessionManager(override val count: Long) : SessionManager

private class FakePlatformInterstitialAd(var ready: Boolean = false) : PlatformInterstitialAd {
    var loadCount = 0
    var showCount = 0
    override fun load() {
        loadCount++
    }

    override fun isReady(): Boolean = ready
    override fun show() {
        showCount++
    }
}

private class FakeClock(var epochSeconds: Long) : Clock {
    override fun now(): Instant = Instant.fromEpochSeconds(epochSeconds)
}

private const val PAST_COOLDOWN = 120L // > the manager's 15s cooldown
private const val PAST_GRACE = 10L // > the manager's GRACE_SESSIONS
private const val LAST_GRACE_SESSION = 3L // == GRACE_SESSIONS, still within the grace window
private const val FIRST_PAID_SESSION = 4L // GRACE_SESSIONS + 1, first session that may show an ad

class InterstitialManagerTest {

    private val platform = FakePlatformInterstitialAd()
    private val clock = FakeClock(epochSeconds = 1_000_000)

    private fun manager(
        premium: Boolean = false,
        demo: Boolean = false,
        sessionCount: Long = PAST_GRACE
    ) = InterstitialManager(
        FakePremiumManager(premium),
        platform,
        FakeDemoManager(demo),
        FakeSessionManager(sessionCount),
        clock
    )

    @Test
    fun preload_loads_when_eligible() {
        manager().preload()
        assertEquals(1, platform.loadCount)
    }

    @Test
    fun preload_does_not_load_when_premium() {
        manager(premium = true).preload()
        assertEquals(0, platform.loadCount)
    }

    @Test
    fun preload_does_not_load_in_demo_mode() {
        manager(demo = true).preload()
        assertEquals(0, platform.loadCount)
    }

    @Test
    fun preload_does_not_load_during_grace_sessions() {
        manager(sessionCount = LAST_GRACE_SESSION).preload()
        assertEquals(0, platform.loadCount)
    }

    @Test
    fun transition_shows_once_after_cooldown_when_ready() {
        platform.ready = true
        val interstitial = manager()

        clock.epochSeconds += PAST_COOLDOWN
        interstitial.onTransition()
        assertEquals(1, platform.showCount)

        // Once per session: a second transition does nothing.
        interstitial.onTransition()
        assertEquals(1, platform.showCount)
    }

    @Test
    fun transition_does_not_show_before_cooldown() {
        platform.ready = true
        manager().onTransition() // no time advanced -> still within cooldown
        assertEquals(0, platform.showCount)
    }

    @Test
    fun transition_does_not_show_when_not_ready() {
        platform.ready = false
        val interstitial = manager()
        clock.epochSeconds += PAST_COOLDOWN
        interstitial.onTransition()
        assertEquals(0, platform.showCount)
    }

    @Test
    fun transition_does_not_show_when_premium() {
        platform.ready = true
        val interstitial = manager(premium = true)
        clock.epochSeconds += PAST_COOLDOWN
        interstitial.onTransition()
        assertEquals(0, platform.showCount)
    }

    @Test
    fun transition_does_not_show_in_demo_mode() {
        platform.ready = true
        val interstitial = manager(demo = true)
        clock.epochSeconds += PAST_COOLDOWN
        interstitial.onTransition()
        assertEquals(0, platform.showCount)
    }

    @Test
    fun transition_does_not_show_during_grace_sessions() {
        platform.ready = true
        val interstitial = manager(sessionCount = LAST_GRACE_SESSION)
        clock.epochSeconds += PAST_COOLDOWN
        interstitial.onTransition()
        assertEquals(0, platform.showCount)
    }

    @Test
    fun transition_shows_on_first_session_after_grace() {
        platform.ready = true
        val interstitial = manager(sessionCount = FIRST_PAID_SESSION)
        clock.epochSeconds += PAST_COOLDOWN
        interstitial.onTransition()
        assertEquals(1, platform.showCount)
    }
}
