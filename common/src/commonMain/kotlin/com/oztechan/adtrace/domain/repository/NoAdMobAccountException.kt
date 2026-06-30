/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.domain.repository

/** Thrown when the signed-in Google account has no associated AdMob publisher account. */
class NoAdMobAccountException : Exception(
    "No AdMob account is linked to this Google account. " +
        "Sign in with the Google account that owns your AdMob publisher account."
)
