/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.core.crypto

import org.kotlincrypto.hash.sha2.SHA256
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/** Generates random bytes from a cryptographically secure source (platform-backed). */
expect fun secureRandomBytes(size: Int): ByteArray

/**
 * PKCE (RFC 7636) helpers + OAuth `state`. The verifier is base64url-without-padding of random
 * bytes; the challenge is base64url-without-padding of SHA-256(verifier).
 */
@OptIn(ExperimentalEncodingApi::class)
object Pkce {

    private const val VERIFIER_BYTES = 64
    private const val STATE_BYTES = 16

    private val base64Url = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT)

    fun createCodeVerifier(): String = base64Url.encode(secureRandomBytes(VERIFIER_BYTES))

    fun codeChallengeFor(verifier: String): String =
        base64Url.encode(SHA256().digest(verifier.encodeToByteArray()))

    fun createState(): String = base64Url.encode(secureRandomBytes(STATE_BYTES))
}
