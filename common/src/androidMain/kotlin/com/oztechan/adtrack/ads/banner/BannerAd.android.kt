/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ads.banner

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.oztechan.adtrack.config.BuildKonfig
import org.koin.compose.koinInject

@Composable
actual fun PlatformBanner(modifier: Modifier) {
    val factory = koinInject<AndroidBannerFactory>()
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context -> factory.create(context, BuildKonfig.ADMOB_BANNER_UNIT_ID) }
    )
}
