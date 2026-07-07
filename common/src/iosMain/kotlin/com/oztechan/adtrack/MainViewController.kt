/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack

import androidx.compose.ui.window.ComposeUIViewController
import com.oztechan.adtrack.di.initKoin
import platform.UIKit.UIViewController

/** Entry point consumed by the iOS Xcode project. */
fun mainViewController(): UIViewController = ComposeUIViewController { AdTrackApp() }

/**
 * Called once from the Swift `@main` app to start dependency injection.
 * Note: must NOT be named with an `init` prefix — Objective-C/Swift interop treats `init*` names
 * as initializers, which prevents Kotlin from exporting it as a callable static member.
 */
fun startKoin() {
    initKoin()
}
