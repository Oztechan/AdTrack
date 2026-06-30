/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.domain

import com.oztechan.adtrace.data.admob.model.ApiDate
import com.oztechan.adtrace.domain.model.Period
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PeriodCalculatorTest {

    private val calculator = PeriodCalculator()
    private val tz = "UTC"

    private fun ApiDate.toLocalDate() = LocalDate(year, month, day)

    @Test
    fun today_range_is_a_single_day() {
        val range = calculator.currentRange(Period.TODAY, tz)
        assertEquals(range.startDate, range.endDate)
    }

    @Test
    fun last_30_days_spans_thirty_days() {
        val range = calculator.currentRange(Period.LAST_30_DAYS, tz)
        assertEquals(29, range.startDate.toLocalDate().daysUntil(range.endDate.toLocalDate()))
    }

    @Test
    fun last_90_days_spans_ninety_days() {
        val range = calculator.currentRange(Period.LAST_90_DAYS, tz)
        assertEquals(89, range.startDate.toLocalDate().daysUntil(range.endDate.toLocalDate()))
    }

    @Test
    fun last_365_days_spans_a_year() {
        val range = calculator.currentRange(Period.LAST_365_DAYS, tz)
        assertEquals(364, range.startDate.toLocalDate().daysUntil(range.endDate.toLocalDate()))
    }

    @Test
    fun lifetime_starts_at_a_fixed_epoch_and_ends_today_with_no_previous() {
        val today = calculator.currentRange(Period.TODAY, tz).endDate
        val range = calculator.currentRange(Period.LIFETIME, tz)

        assertEquals(2010, range.startDate.year)
        assertEquals(1, range.startDate.month)
        assertEquals(1, range.startDate.day)
        assertEquals(today, range.endDate)
        assertNull(calculator.previousRange(Period.LIFETIME, tz))
    }

    @Test
    fun previous_today_is_the_day_before_current() {
        val current = calculator.currentRange(Period.TODAY, tz)
        val previous = assertNotNull(calculator.previousRange(Period.TODAY, tz))
        assertEquals(previous.startDate, previous.endDate)
        assertEquals(1, previous.endDate.toLocalDate().daysUntil(current.startDate.toLocalDate()))
    }

    @Test
    fun previous_rolling_window_is_adjacent_and_same_length() {
        val current = calculator.currentRange(Period.LAST_30_DAYS, tz)
        val previous = assertNotNull(calculator.previousRange(Period.LAST_30_DAYS, tz))
        assertEquals(29, previous.startDate.toLocalDate().daysUntil(previous.endDate.toLocalDate()))
        // previous ends the day before the current window starts.
        assertEquals(1, previous.endDate.toLocalDate().daysUntil(current.startDate.toLocalDate()))
    }

    @Test
    fun invalid_timezone_falls_back_to_utc_without_throwing() {
        val range = calculator.currentRange(Period.TODAY, "Not/AZone")
        assertEquals(range.startDate, range.endDate)
    }
}
