/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.android

import android.app.Application
import com.oztechan.adtrace.di.initKoin
import org.koin.android.ext.koin.androidContext

class AdTraceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@AdTraceApplication)
        }
    }
}
