/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.core.util

import java.text.NumberFormat
import java.util.Currency

// Fallback rounds to two decimal places when the currency code is unknown to the platform.
private const val CENTS = 100

@Suppress("TooGenericExceptionCaught", "SwallowedException")
actual fun formatCurrency(amount: Double, currencyCode: String): String = try {
    NumberFormat.getCurrencyInstance().apply {
        currency = Currency.getInstance(currencyCode)
    }.format(amount)
} catch (e: Exception) {
    "$currencyCode ${(amount * CENTS).toLong() / CENTS.toDouble()}"
}
