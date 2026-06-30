/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrace.ui.feature.signin

import androidx.lifecycle.viewModelScope
import com.oztechan.adtrace.core.viewmodel.SEEDViewModel
import com.oztechan.adtrace.data.auth.browser.AuthCancelledException
import com.oztechan.adtrace.domain.repository.AuthRepository
import kotlinx.coroutines.launch

class SignInViewModel(
    private val authRepository: AuthRepository
) : SEEDViewModel<SignInState, SignInEffect, SignInEvent, SignInData>(
    initialState = SignInState(),
    initialData = SignInData()
),
    SignInEvent {

    override fun onSignInClick() {
        if (state.value.isLoading) return
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            authRepository.signIn()
                .onSuccess { setEffect { SignInEffect.NavigateToDashboard } }
                .onFailure { error ->
                    if (error !is AuthCancelledException) {
                        setEffect { SignInEffect.ShowError(error.message ?: "Sign-in failed") }
                    }
                }
            setState { copy(isLoading = false) }
        }
    }
}
