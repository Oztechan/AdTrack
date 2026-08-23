/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.banner

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.oztechan.adtrack.ads.premium.PremiumManager
import org.koin.compose.koinInject

/**
 * The native anchored-adaptive banner ad, rendered per platform (Android `AdView` / iOS
 * `GADBannerView`). The ad SDK stays in the app modules; each platform provides the view through a
 * banner factory injected via Koin, so this module never depends on the ad SDK.
 */
@Composable
expect fun PlatformBanner(modifier: Modifier)

/**
 * A banner ad that hides itself while the user has premium. Screens place this in their bottom bar;
 * the premium gate is the one shared decision, and it reacts to grants (e.g. the rewarded ad) and
 * expiry through [PremiumManager.isPremium].
 *
 * The bottom safe-area inset (iOS home indicator / Android navigation bar) is applied so the ad never
 * sits under the gesture area — AdMob prohibits placements prone to accidental taps.
 */
@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    val premiumManager = koinInject<PremiumManager>()
    val isPremium by premiumManager.isPremium.collectAsStateWithLifecycle()

    if (!isPremium) {
        PlatformBanner(
            modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
        )
    }
}
