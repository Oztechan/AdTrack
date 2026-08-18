/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.banner

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import com.oztechan.adtrack.config.BuildKonfig
import org.koin.compose.koinInject

// Anchored adaptive banners are at most this tall on phones; the container reserves the space.
private const val BANNER_HEIGHT_DP = 60

@Composable
actual fun PlatformBanner(modifier: Modifier) {
    val factory = koinInject<IosBannerFactory>()
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val width = maxWidth.value.toDouble()
        UIKitView(
            factory = { factory.create(BuildKonfig.ADMOB_BANNER_UNIT_ID, width) },
            modifier = Modifier.fillMaxWidth().height(BANNER_HEIGHT_DP.dp)
        )
    }
}
