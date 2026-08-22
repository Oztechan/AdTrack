/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.rewarded

import com.oztechan.adtrack.ads.premium.PremiumManagerImpl
import com.oztechan.adtrack.core.storage.SecureStorage
import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RewardedAdManagerTest {

    private val premiumManager = PremiumManagerImpl(SecureStorage(MapSettings()))
    private val manager = RewardedAdManager(premiumManager)

    @Test
    fun starts_idle() {
        assertEquals(RewardedAdState.IDLE, manager.state.value)
    }

    @Test
    fun loading_then_finished_returns_to_idle() {
        manager.onLoading()
        assertEquals(RewardedAdState.LOADING, manager.state.value)

        manager.onFinished()
        assertEquals(RewardedAdState.IDLE, manager.state.value)
    }

    @Test
    fun failure_is_reflected() {
        manager.onLoading()
        manager.onFailed()
        assertEquals(RewardedAdState.FAILED, manager.state.value)
    }

    @Test
    fun earning_the_reward_grants_premium() {
        assertFalse(premiumManager.isPremium.value)

        manager.onRewardEarned()

        assertTrue(premiumManager.isPremium.value)
    }
}
