/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ui.components

import com.oztechan.adtrack.domain.model.Period
import kotlin.test.Test
import kotlin.test.assertEquals

class PeriodLabelsTest {

    @Test
    fun label_maps_every_period_to_its_display_name() {
        assertEquals("Today", Period.TODAY.label())
        assertEquals("Last 30 Days", Period.LAST_30_DAYS.label())
        assertEquals("Last 90 Days", Period.LAST_90_DAYS.label())
        assertEquals("Last 365 Days", Period.LAST_365_DAYS.label())
        assertEquals("Lifetime", Period.LIFETIME.label())
    }

    @Test
    fun label_is_defined_for_all_periods() {
        // Guards against an unmapped enum entry being added later.
        Period.entries.forEach { period ->
            assertEquals(true, period.label().isNotBlank())
        }
    }

    @Test
    fun selector_label_names_both_days_only_for_today() {
        assertEquals("Today/Yesterday", Period.TODAY.selectorLabel())
        // Every other period reuses its plain label.
        Period.entries.filterNot { it == Period.TODAY }.forEach { period ->
            assertEquals(period.label(), period.selectorLabel())
        }
    }

    @Test
    fun series_title_matches_the_granularity_of_each_period() {
        assertEquals("Daily earnings", Period.TODAY.seriesTitle())
        assertEquals("Daily earnings", Period.LAST_30_DAYS.seriesTitle())
        assertEquals("Daily earnings", Period.LAST_90_DAYS.seriesTitle())
        assertEquals("Weekly earnings", Period.LAST_365_DAYS.seriesTitle())
        assertEquals("Monthly earnings", Period.LIFETIME.seriesTitle())
    }
}
