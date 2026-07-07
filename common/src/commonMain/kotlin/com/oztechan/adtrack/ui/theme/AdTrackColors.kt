/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Semantic colors that Material 3 has no direct role for (revenue up/down). */
@Immutable
data class AdTrackColors(
    val positive: Color,
    val negative: Color
)

val LocalAdTrackColors = staticCompositionLocalOf {
    AdTrackColors(positive = Color.Unspecified, negative = Color.Unspecified)
}
