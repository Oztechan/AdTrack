/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.domain.repository

interface AuthRepository {
    fun isSignedIn(): Boolean
    suspend fun signIn(): Result<Unit>
    fun signOut()
}
