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
    fun last_7_days_spans_seven_days() {
        val range = calculator.currentRange(Period.LAST_7_DAYS, tz)
        // start..end inclusive of 7 days => 6 days between them.
        assertEquals(6, range.startDate.toLocalDate().daysUntil(range.endDate.toLocalDate()))
    }

    @Test
    fun last_90_days_spans_ninety_days() {
        val range = calculator.currentRange(Period.LAST_90_DAYS, tz)
        assertEquals(89, range.startDate.toLocalDate().daysUntil(range.endDate.toLocalDate()))
    }

    @Test
    fun this_month_starts_on_the_first() {
        val range = calculator.currentRange(Period.THIS_MONTH, tz)
        assertEquals(1, range.startDate.day)
        assertEquals(range.endDate.month, range.startDate.month)
        assertEquals(range.endDate.year, range.startDate.year)
    }

    @Test
    fun previous_today_is_the_day_before_current() {
        val current = calculator.currentRange(Period.TODAY, tz)
        val previous = calculator.previousRange(Period.TODAY, tz)
        assertEquals(previous.startDate, previous.endDate)
        assertEquals(
            1,
            previous.endDate.toLocalDate().daysUntil(current.startDate.toLocalDate())
        )
    }

    @Test
    fun previous_last_7_days_is_adjacent_and_same_length() {
        val current = calculator.currentRange(Period.LAST_7_DAYS, tz)
        val previous = calculator.previousRange(Period.LAST_7_DAYS, tz)
        assertEquals(6, previous.startDate.toLocalDate().daysUntil(previous.endDate.toLocalDate()))
        // previous ends the day before the current window starts.
        assertEquals(
            1,
            previous.endDate.toLocalDate().daysUntil(current.startDate.toLocalDate())
        )
    }

    @Test
    fun invalid_timezone_falls_back_to_utc_without_throwing() {
        val range = calculator.currentRange(Period.TODAY, "Not/AZone")
        assertEquals(range.startDate, range.endDate)
    }
}
