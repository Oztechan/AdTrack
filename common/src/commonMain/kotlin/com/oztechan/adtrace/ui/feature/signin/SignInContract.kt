/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.ui.feature.signin

import com.oztechan.adtrace.core.viewmodel.BaseData
import com.oztechan.adtrace.core.viewmodel.BaseEffect
import com.oztechan.adtrace.core.viewmodel.BaseEvent
import com.oztechan.adtrace.core.viewmodel.BaseState

data class SignInState(
    val isLoading: Boolean = false
) : BaseState

sealed interface SignInEffect : BaseEffect {
    data object NavigateToDashboard : SignInEffect
    data class ShowError(val message: String) : SignInEffect
}

interface SignInEvent : BaseEvent {
    fun onSignInClick()
}

class SignInData : BaseData
