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
    fun first_launch_counts_as_session_one() {
        val manager = SessionManagerImpl(PreferenceStorage(MapSettings()))
        assertEquals(1L, manager.count)
    }

    @Test
    fun each_new_instance_increments_the_persisted_count() {
        // A fresh instance per launch, sharing the same backing store, keeps counting up.
        val settings: Settings = MapSettings()

        assertEquals(1L, SessionManagerImpl(PreferenceStorage(settings)).count)
        assertEquals(2L, SessionManagerImpl(PreferenceStorage(settings)).count)
        assertEquals(3L, SessionManagerImpl(PreferenceStorage(settings)).count)
    }

    @Test
    fun count_is_stable_within_a_single_instance() {
        val manager = SessionManagerImpl(PreferenceStorage(MapSettings()))
        assertEquals(manager.count, manager.count)
    }
}
