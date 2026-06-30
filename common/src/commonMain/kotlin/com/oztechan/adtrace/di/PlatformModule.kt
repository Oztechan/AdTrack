/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.di

import org.koin.core.module.Module

/** Provides platform-specific singletons: the secure Settings store and the OAuth browser launcher. */
expect val platformModule: Module
