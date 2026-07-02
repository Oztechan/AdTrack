/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Semantic colors that Material 3 has no direct role for (revenue up/down). */
@Immutable
data class AdTraceColors(
    val positive: Color,
    val negative: Color
)

val LocalAdTraceColors = staticCompositionLocalOf {
    AdTraceColors(positive = Color.Unspecified, negative = Color.Unspecified)
}
