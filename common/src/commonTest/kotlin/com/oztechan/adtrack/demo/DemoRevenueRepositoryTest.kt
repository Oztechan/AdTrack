/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.demo

import com.oztechan.adtrack.domain.model.Period
import com.oztechan.adtrack.fakes.FakeRevenueRepository
import com.oztechan.adtrack.fakes.fakeAccount
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DemoRevenueRepositoryTest {

    private val demoManager = DemoManagerImpl()
    private val real = FakeRevenueRepository(account = fakeAccount(publisherId = "pub-real"))
    private val repository = DemoRevenueRepository(demoManager, real)

    @Test
    fun delegates_to_real_when_not_in_demo() = runTest {
        assertEquals("pub-real", repository.getAccount().publisherId)
    }

    @Test
    fun serves_sample_data_when_in_demo() = runTest {
        demoManager.enterDemo()

        assertEquals(DemoData.account.publisherId, repository.getAccount().publisherId)
        assertTrue(repository.getSummary(Period.TODAY).earnings > 0)
        assertTrue(repository.getAppBreakdown(Period.LAST_30_DAYS).isNotEmpty())
        assertTrue(repository.getRevenueSeries(Period.LAST_30_DAYS).isNotEmpty())
        assertTrue(repository.getFormatBreakdown(Period.TODAY, "demo-weather-android").isNotEmpty())
    }

    @Test
    fun invalidate_always_delegates_to_real() {
        demoManager.enterDemo()
        repository.invalidate()
        assertTrue(real.invalidateCalled)
    }
}
