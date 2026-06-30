/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.data.auth.browser

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AuthenticationServices.ASPresentationAnchor
import platform.AuthenticationServices.ASWebAuthenticationPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASWebAuthenticationSession
import platform.AuthenticationServices.ASWebAuthenticationSessionErrorCodeCanceledLogin
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IosAuthBrowserLauncher : AuthBrowserLauncher {

    private val presentationContextProvider = object :
        NSObject(), ASWebAuthenticationPresentationContextProvidingProtocol {
        override fun presentationAnchorForWebAuthenticationSession(
            session: ASWebAuthenticationSession
        ): ASPresentationAnchor = keyWindow()
    }

    override suspend fun authenticate(
        authUrl: String,
        callbackScheme: String
    ): String = suspendCancellableCoroutine { continuation ->
        val url = NSURL(string = authUrl)
        val session = ASWebAuthenticationSession(
            uRL = url,
            callbackURLScheme = callbackScheme
        ) { callbackUrl, error ->
            when {
                callbackUrl != null ->
                    continuation.resume(callbackUrl.absoluteString.orEmpty())

                error != null && error.code == ASWebAuthenticationSessionErrorCodeCanceledLogin ->
                    continuation.resumeWithException(AuthCancelledException())

                else ->
                    continuation.resumeWithException(
                        AuthException(error?.localizedDescription ?: "Authentication failed")
                    )
            }
        }
        session.presentationContextProvider = presentationContextProvider
        session.prefersEphemeralWebBrowserSession = false
        continuation.invokeOnCancellation { session.cancel() }
        session.start()
    }

    private fun keyWindow(): UIWindow =
        UIApplication.sharedApplication.windows
            .filterIsInstance<UIWindow>()
            .firstOrNull { it.isKeyWindow() }
            ?: UIApplication.sharedApplication.windows.filterIsInstance<UIWindow>().first()
}
