/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ui.feature.settings

import androidx.lifecycle.viewModelScope
import com.oztechan.adtrack.core.viewmodel.SEEDViewModel
import com.oztechan.adtrack.domain.repository.AuthRepository
import com.oztechan.adtrack.domain.repository.RevenueRepository
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authRepository: AuthRepository,
    private val revenueRepository: RevenueRepository
) : SEEDViewModel<SettingsState, SettingsEffect, SettingsEvent, SettingsData>(
    initialState = SettingsState(),
    initialData = SettingsData()
),
    SettingsEvent {

    init {
        load()
    }

    override fun onRetry() = load()

    override fun onBackClick() = sendEffect { SettingsEffect.NavigateBack }

    override fun onSignOutClick() {
        authRepository.signOut()
        revenueRepository.invalidate()
        sendEffect { SettingsEffect.NavigateToSignIn }
    }

    private fun load() {
        viewModelScope.launch {
            setState { copy(isLoading = true, errorMessage = null) }
            runCatching { revenueRepository.getAccount() }
                .onSuccess { account ->
                    setState {
                        copy(
                            isLoading = false,
                            publisherId = account.publisherId,
                            currencyCode = account.currencyCode,
                            reportingTimeZone = account.reportingTimeZone
                        )
                    }
                }
                .onFailure { error ->
                    setState { copy(isLoading = false, errorMessage = error.message ?: "Failed to load account") }
                }
        }
    }
}
