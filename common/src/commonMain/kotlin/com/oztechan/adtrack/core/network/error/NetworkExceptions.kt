/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.core.network.error

import kotlinx.coroutines.CancellationException

class NetworkException(cause: Throwable) : Throwable(cause)

class UnknownNetworkException(cause: Throwable) : Throwable(cause)

class TimeoutException(cause: Throwable) : Throwable(cause)

class ModelMappingException(cause: Throwable) : Throwable(cause)

// Maps coroutine cancellation into the project's error taxonomy. Extends CancellationException (not
// Throwable) so structured concurrency still recognises it as cancellation and unwinds cleanly.
class TerminationException(cause: Throwable) : CancellationException(cause.message)

class UnauthorizedException(cause: Throwable) : Throwable(cause)

class EmptyParameterException : Exception("parameter can not be empty")
