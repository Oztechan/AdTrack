/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.rewarded

import com.oztechan.adtrack.ads.premium.PremiumManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Duration.Companion.days

/**
 * Shared owner of the rewarded-ad flow: exposes the reactive [state] the UI observes, and applies the
 * one shared decision — watching the ad grants a fixed ad-free window via [PremiumManager]. The
 * per-platform [PlatformRewardedAd] reports its lifecycle here by calling the `on*` methods.
 */
class RewardedAdManager(private val premiumManager: PremiumManager) {

    private val _state = MutableStateFlow(RewardedAdState.IDLE)
    val state: StateFlow<RewardedAdState> = _state.asStateFlow()

    /** The ad started loading/showing. */
    fun onLoading() {
        _state.value = RewardedAdState.LOADING
    }

    /** The user watched the ad to completion — grant the ad-free window. */
    fun onRewardEarned() {
        premiumManager.grantPremium(REWARD_WINDOW)
    }

    /** The ad was dismissed (with or without a reward); back to idle. */
    fun onFinished() {
        _state.value = RewardedAdState.IDLE
    }

    /** The ad failed to load or show. */
    fun onFailed() {
        _state.value = RewardedAdState.FAILED
    }

    private companion object {
        val REWARD_WINDOW = 2.days
    }
}
