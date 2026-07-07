/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.data.auth.browser

import kotlinx.coroutines.CompletableDeferred

/**
 * Bridges the Custom Tabs redirect (delivered as a fresh Intent to [RedirectActivity]) back into the
 * suspending [AndroidAuthBrowserLauncher.authenticate] call. Custom Tabs has no cancel callback, so
 * the host activity calls [reconcileOnResume] when it returns to the foreground without a redirect.
 */
class AuthRedirectBus {

    private var deferred: CompletableDeferred<String>? = null
    private var awaitingRedirect = false

    fun start(): CompletableDeferred<String> {
        awaitingRedirect = true
        return CompletableDeferred<String>().also { deferred = it }
    }

    fun onRedirect(url: String) {
        awaitingRedirect = false
        deferred?.complete(url)
        deferred = null
    }

    /** If we came back to the app without a redirect having arrived, the user cancelled. */
    fun reconcileOnResume() {
        if (awaitingRedirect) {
            awaitingRedirect = false
            deferred?.completeExceptionally(AuthCancelledException())
            deferred = null
        }
    }
}
