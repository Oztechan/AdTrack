/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.core.session

import com.oztechan.adtrack.core.storage.PreferenceStorage
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
import kotlin.test.Test
import kotlin.test.assertEquals

class SessionManagerImplTest {

    @Test
    fun count_starts_at_zero_before_any_session() {
        val manager = SessionManagerImpl(PreferenceStorage(MapSettings()))
        assertEquals(0L, manager.count)
    }

    @Test
    fun registering_sessions_increments_the_persisted_count() {
        val manager = SessionManagerImpl(PreferenceStorage(MapSettings()))

        manager.registerSession()
        assertEquals(1L, manager.count)

        manager.registerSession()
        assertEquals(2L, manager.count)
    }

    @Test
    fun reading_count_does_not_advance_it() {
        val manager = SessionManagerImpl(PreferenceStorage(MapSettings()))
        manager.registerSession()

        // Reading repeatedly must not change the count.
        assertEquals(1L, manager.count)
        assertEquals(1L, manager.count)
        assertEquals(1L, manager.count)
    }

    @Test
    fun constructing_a_new_instance_does_not_advance_the_count() {
        // The key safety property: only registerSession() advances; construction has no side effect.
        val settings: Settings = MapSettings()
        SessionManagerImpl(PreferenceStorage(settings)).registerSession()

        // A fresh instance (e.g. next launch, or an incidental inject just to read) sees 1, not 2.
        assertEquals(1L, SessionManagerImpl(PreferenceStorage(settings)).count)
    }
}
