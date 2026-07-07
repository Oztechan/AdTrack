/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.android

import android.app.Application
import com.oztechan.adtrack.di.initKoin
import org.koin.android.ext.koin.androidContext

class AdTrackApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@AdTrackApplication)
        }
    }
}
