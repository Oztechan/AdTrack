/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.data.auth.browser

/**
 * Opens the system browser at [authUrl] and suspends until the app is re-entered via the OAuth
 * redirect URI, returning the full callback URL. Android uses Chrome Custom Tabs + a redirect
 * activity; iOS uses ASWebAuthenticationSession.
 */
interface AuthBrowserLauncher {
    suspend fun authenticate(authUrl: String, callbackScheme: String): String
}

/** Thrown when the user dismisses the OAuth browser without completing sign-in. */
class AuthCancelledException : Exception("Authentication was cancelled")

/** Thrown when the OAuth flow fails (bad state, missing code, browser error). */
class AuthException(message: String) : Exception(message)
