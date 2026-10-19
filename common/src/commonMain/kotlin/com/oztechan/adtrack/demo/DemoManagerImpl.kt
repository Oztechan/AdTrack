/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.demo

class DemoManagerImpl : DemoManager {

    override var isDemo: Boolean = false
        private set

    override fun enterDemo() {
        isDemo = true
    }

    override fun exitDemo() {
        isDemo = false
    }
}
