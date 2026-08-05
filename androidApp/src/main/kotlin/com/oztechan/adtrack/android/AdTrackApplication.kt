/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.android

import android.app.Application
import com.github.submob.logmob.enableCrashlyticsCollection
import com.github.submob.logmob.initLogger
import com.google.android.gms.ads.MobileAds
import com.oztechan.adtrack.di.initKoin
import org.koin.android.ext.koin.androidContext
import kotlin.concurrent.thread

class AdTrackApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        enableCrashlyticsCollection()
        initLogger()

        initKoin {
            androidContext(this@AdTrackApplication)
        }

        // Google recommends initializing the Mobile Ads SDK off the main thread, as it does I/O.
        thread { MobileAds.initialize(this) }
    }
}
