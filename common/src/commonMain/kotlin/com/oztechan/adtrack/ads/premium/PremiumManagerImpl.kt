/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.premium

import com.oztechan.adtrack.core.storage.SecureStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Clock
import kotlin.time.Duration

/**
 * Premium = an ad-free window whose expiry (epoch seconds) is persisted in [storage]. The user has
 * premium while now is before that expiry. Times are kept as epoch seconds (like the token store),
 * and [clock] is injectable for testing.
 */
class PremiumManagerImpl(
    private val storage: SecureStorage,
    private val clock: Clock = Clock.System
) : PremiumManager {

    private val _isPremium = MutableStateFlow(computeIsPremium())
    override val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    override fun grantPremium(duration: Duration) {
        // Extend from the later of now or an existing window, so repeated grants stack.
        val base = maxOf(storedExpirySeconds(), clock.now().epochSeconds)
        storage.putLong(KEY_PREMIUM_UNTIL, base + duration.inWholeSeconds)
        refresh()
    }

    override fun refresh() {
        _isPremium.value = computeIsPremium()
    }

    private fun computeIsPremium(): Boolean = clock.now().epochSeconds < storedExpirySeconds()

    private fun storedExpirySeconds(): Long = storage.getLong(KEY_PREMIUM_UNTIL) ?: 0L

    private companion object {
        const val KEY_PREMIUM_UNTIL = "premium_until_epoch_seconds"
    }
}
