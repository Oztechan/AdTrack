/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.data.auth.token

/**
 * Supplies bearer tokens to the authenticated [io.ktor.client.HttpClient].
 * Implemented by the token store so the network layer stays decoupled from auth internals.
 */
interface TokenProvider {
    /** Returns a currently valid access token, refreshing if necessary, or null if signed out. */
    suspend fun validAccessToken(): String?

    /** Forces a refresh using the stored refresh token. Returns the new access token or null. */
    suspend fun refreshAccessToken(): String?
}
