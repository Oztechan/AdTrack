/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack

import androidx.compose.ui.window.ComposeUIViewController
import com.github.submob.logmob.initCrashlytics
import com.github.submob.logmob.initLogger
import com.oztechan.adtrack.ads.banner.IosBannerFactory
import com.oztechan.adtrack.ads.rewarded.PlatformRewardedAd
import com.oztechan.adtrack.ads.rewarded.RewardedAdConfig
import com.oztechan.adtrack.ads.rewarded.RewardedAdManager
import com.oztechan.adtrack.di.initKoin
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import platform.UIKit.UIViewController

/** Entry point consumed by the iOS Xcode project. */
fun mainViewController(): UIViewController = ComposeUIViewController { AdTrackApp() }

/**
 * Called once from the Swift `@main` app, AFTER `FirebaseApp.configure()`, to route Kermit logs to
 * Crashlytics and install the unhandled-exception hook.
 * Note: must NOT be named with an `init` prefix — Objective-C/Swift interop treats `init*` names
 * as initializers, which prevents Kotlin from exporting it as a callable static member.
 */
fun startCrashlytics() {
    initLogger()
    initCrashlytics()
}

/**
 * Called once from the Swift `@main` app to start dependency injection. The Swift-built
 * [bannerFactory] and [rewardedAd] are registered here so the shared ad code can inject them — the
 * iOS Mobile Ads SDK is linked only into the Xcode app target, not this framework.
 * Note: must NOT be named with an `init` prefix — Objective-C/Swift interop treats `init*` names
 * as initializers, which prevents Kotlin from exporting it as a callable static member.
 */
fun startKoin(bannerFactory: IosBannerFactory, rewardedAd: PlatformRewardedAd) {
    initKoin()
    loadKoinModules(
        module {
            single { bannerFactory }
            single { rewardedAd }
        }
    )
}

/** For the Swift rewarded-ad impl to report its lifecycle into the shared manager. */
fun rewardedAdManager(): RewardedAdManager = KoinPlatform.getKoin().get()

/** Rewarded ad unit id (Google test id by default) for the Swift rewarded-ad impl. */
fun rewardedAdUnitId(): String = KoinPlatform.getKoin().get<RewardedAdConfig>().adUnitId
