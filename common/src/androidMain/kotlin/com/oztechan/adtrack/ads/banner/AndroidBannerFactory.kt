/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.banner

import android.content.Context
import android.view.View

/**
 * Builds the Android banner view (`AdView`). Implemented in the app module so the Mobile Ads SDK
 * stays out of this module; the width for the anchored adaptive banner is read from the [context].
 */
interface AndroidBannerFactory {
    fun create(context: Context, adUnitId: String): View
}
