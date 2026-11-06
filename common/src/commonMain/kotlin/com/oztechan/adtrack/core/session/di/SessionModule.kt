/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.core.session.di

import com.oztechan.adtrack.core.session.SessionManager
import com.oztechan.adtrack.core.session.SessionManagerImpl
import org.koin.dsl.module

val sessionModule = module {
    single<SessionManager> { SessionManagerImpl(get()) }
}
