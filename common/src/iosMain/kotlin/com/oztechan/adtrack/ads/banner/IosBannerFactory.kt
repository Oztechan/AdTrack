/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.banner

import platform.UIKit.UIView

/**
 * Builds the iOS banner view (`GADBannerView`). Implemented in Swift (the Mobile Ads SDK is linked
 * only into the Xcode app target), conforming to this exported interface; [width] sizes an anchored
 * adaptive banner to the available Compose width.
 */
interface IosBannerFactory {
    fun create(adUnitId: String, width: Double): UIView
}
