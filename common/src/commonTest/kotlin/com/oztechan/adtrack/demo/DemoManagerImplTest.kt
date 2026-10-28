/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.demo

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DemoManagerImplTest {

    @Test
    fun defaults_to_not_demo() {
        assertFalse(DemoManagerImpl().isDemo)
    }

    @Test
    fun enter_demo_turns_it_on() {
        val manager = DemoManagerImpl()
        manager.enterDemo()
        assertTrue(manager.isDemo)
    }

    @Test
    fun exit_demo_turns_it_off() {
        val manager = DemoManagerImpl()
        manager.enterDemo()
        manager.exitDemo()
        assertFalse(manager.isDemo)
    }
}
