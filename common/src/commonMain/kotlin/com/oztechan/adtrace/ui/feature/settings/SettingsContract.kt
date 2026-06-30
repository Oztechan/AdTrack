/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.ui.feature.settings

import com.oztechan.adtrace.core.viewmodel.BaseData
import com.oztechan.adtrace.core.viewmodel.BaseEffect
import com.oztechan.adtrace.core.viewmodel.BaseEvent
import com.oztechan.adtrace.core.viewmodel.BaseState

data class SettingsState(
    val isLoading: Boolean = true,
    val publisherId: String = "",
    val currencyCode: String = "",
    val reportingTimeZone: String = "",
    val errorMessage: String? = null
) : BaseState

sealed interface SettingsEffect : BaseEffect {
    data object NavigateToSignIn : SettingsEffect
    data object NavigateBack : SettingsEffect
}

interface SettingsEvent : BaseEvent {
    fun onSignOutClick()
    fun onBackClick()
    fun onRetry()
}

class SettingsData : BaseData
