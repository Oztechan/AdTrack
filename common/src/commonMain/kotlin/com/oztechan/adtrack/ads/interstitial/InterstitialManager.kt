/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.interstitial

import com.oztechan.adtrack.ads.premium.PremiumManager
import kotlin.time.Clock

/**
 * Decides *whether* a transition-based interstitial may show and drives the per-platform
 * [PlatformInterstitialAd]. Policy: never when premium, at most once per session, and only after a
 * soft cooldown since the session started (so it never fires on the very first quick navigation).
 * Never shown on a timer — only when [onTransition] is called from a real navigation transition.
 */
class InterstitialManager(
    private val premiumManager: PremiumManager,
    private val platformInterstitialAd: PlatformInterstitialAd,
    private val clock: Clock = Clock.System
) {

    private var shownThisSession = false
    private val sessionStartEpochSeconds = clock.now().epochSeconds

    /** Preload ahead of a transition (e.g. when the App Detail screen opens) if one may still show. */
    fun preload() {
        if (canOffer()) platformInterstitialAd.load()
    }

    /** Called on a real navigation transition; shows the interstitial only if all policy checks pass. */
    fun onTransition() {
        if (canOffer() && cooldownElapsed() && platformInterstitialAd.isReady()) {
            platformInterstitialAd.show()
            shownThisSession = true
        }
    }

    private fun canOffer(): Boolean = !premiumManager.isPremium.value && !shownThisSession

    private fun cooldownElapsed(): Boolean =
        clock.now().epochSeconds - sessionStartEpochSeconds >= COOLDOWN_SECONDS

    private companion object {
        // Short soft cooldown so it never fires on an instant bounce, without over-blocking.
        const val COOLDOWN_SECONDS = 15L
    }
}
