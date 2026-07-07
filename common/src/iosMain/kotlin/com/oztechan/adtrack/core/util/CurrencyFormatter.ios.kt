/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.core.util

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle

actual fun formatCurrency(amount: Double, currencyCode: String): String =
    NSNumberFormatter().apply {
        numberStyle = NSNumberFormatterCurrencyStyle
        this.currencyCode = currencyCode
    }.stringFromNumber(NSNumber(double = amount)) ?: "$currencyCode $amount"
