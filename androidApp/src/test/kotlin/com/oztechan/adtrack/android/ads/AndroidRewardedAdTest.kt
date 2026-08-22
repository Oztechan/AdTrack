/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.android.ads

import android.app.Activity
import android.app.Application
import com.oztechan.adtrack.ads.premium.PremiumManager
import com.oztechan.adtrack.ads.rewarded.RewardedAdConfig
import com.oztechan.adtrack.ads.rewarded.RewardedAdManager
import com.oztechan.adtrack.ads.rewarded.RewardedAdState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

private class FakePremiumManager : PremiumManager {
    override val isPremium: StateFlow<Boolean> = MutableStateFlow(false)
    override fun grantPremium(duration: kotlin.time.Duration) = Unit
    override fun refresh() = Unit
}

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class AndroidRewardedAdTest {

    private val context: Application = Robolectric.buildActivity(Activity::class.java).get().application
    private val manager = RewardedAdManager(FakePremiumManager())
    private val activityHolder = CurrentActivityHolder()

    private fun rewardedAd() = AndroidRewardedAd(
        context = context,
        activityHolder = activityHolder,
        rewardedAdManager = manager,
        config = RewardedAdConfig("ca-app-pub-test/rewarded")
    )

    @Test
    fun show_without_a_resumed_activity_reports_failure() {
        rewardedAd().show() // no activity tracked

        assertEquals(RewardedAdState.FAILED, manager.state.value)
    }

    @Test
    fun show_with_an_activity_starts_loading() {
        val activity = Robolectric.buildActivity(Activity::class.java).get()
        activityHolder.onActivityResumed(activity)

        rewardedAd().show()

        // onLoading() runs before the async load; the SDK never calls back under Robolectric.
        assertEquals(RewardedAdState.LOADING, manager.state.value)
    }
}
