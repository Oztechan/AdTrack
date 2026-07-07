/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.core.crypto

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PkceTest {

    @Test
    fun codeChallenge_matches_rfc7636_test_vector() {
        // From RFC 7636 Appendix B.
        val verifier = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk"
        val expectedChallenge = "E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM"
        assertEquals(expectedChallenge, Pkce.codeChallengeFor(verifier))
    }

    @Test
    fun verifier_is_base64url_without_padding_and_unique() {
        val a = Pkce.createCodeVerifier()
        val b = Pkce.createCodeVerifier()
        assertFalse(a == b, "Two verifiers should differ")
        assertFalse(a.contains('='), "No padding")
        assertFalse(a.contains('+') || a.contains('/'), "URL-safe alphabet only")
        assertTrue(a.length >= 43, "RFC 7636 requires at least 43 chars")
    }

    @Test
    fun state_is_non_empty_and_unique() {
        assertFalse(Pkce.createState() == Pkce.createState())
        assertTrue(Pkce.createState().isNotEmpty())
    }

    @Test
    fun secureRandomBytes_returns_requested_size() {
        assertEquals(32, secureRandomBytes(32).size)
        assertEquals(0, secureRandomBytes(0).size)
    }
}
