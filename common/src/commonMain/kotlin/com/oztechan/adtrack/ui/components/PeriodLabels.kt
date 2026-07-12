/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ui.components

import com.oztechan.adtrack.domain.SeriesGranularity
import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.domain.seriesGranularity

fun Period.label(): String = when (this) {
    Period.TODAY -> "Today"
    Period.LAST_30_DAYS -> "Last 30 Days"
    Period.LAST_90_DAYS -> "Last 90 Days"
    Period.LAST_365_DAYS -> "Last 365 Days"
    Period.LIFETIME -> "Lifetime"
}

// The TODAY view also shows yesterday's card, so its selector chip names both days. Card titles
// and other texts keep the plain [label] since they refer to a single day's data.
internal fun Period.selectorLabel(): String =
    if (this == Period.TODAY) "Today/Yesterday" else label()

/** Chart/list heading matching the series resolution of this period. */
fun Period.seriesTitle(): String = when (seriesGranularity) {
    SeriesGranularity.DAILY -> "Daily earnings"
    SeriesGranularity.WEEKLY -> "Weekly earnings"
    SeriesGranularity.MONTHLY -> "Monthly earnings"
}
