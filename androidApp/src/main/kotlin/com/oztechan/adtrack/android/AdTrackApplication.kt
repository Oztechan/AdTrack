/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.android

import android.app.Application
import com.github.submob.logmob.enableCrashlyticsCollection
import com.github.submob.logmob.initLogger
import com.oztechan.adtrack.android.ads.CurrentActivityHolder
import com.oztechan.adtrack.android.ads.di.androidAdsModule
import com.oztechan.adtrack.di.initKoin
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext

class AdTrackApplication : Application() {

    // Populated for the app's lifetime so the rewarded ad can find the Activity to show from.
    private val activityHolder: CurrentActivityHolder by inject()

    override fun onCreate() {
        super.onCreate()

        enableCrashlyticsCollection()
        initLogger()

        initKoin {
            androidContext(this@AdTrackApplication)
            modules(androidAdsModule)
        }
        registerActivityLifecycleCallbacks(activityHolder)
        // Mobile Ads SDK init happens after UMP consent is gathered (see PlatformConsentManagerImpl,
        // triggered from MainActivity), so we never request ads before consent is resolved.
    }
}
