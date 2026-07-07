/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ui.feature.signin

import com.oztechan.adtrack.core.viewmodel.BaseData
import com.oztechan.adtrack.core.viewmodel.BaseEffect
import com.oztechan.adtrack.core.viewmodel.BaseEvent
import com.oztechan.adtrack.core.viewmodel.BaseState

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
