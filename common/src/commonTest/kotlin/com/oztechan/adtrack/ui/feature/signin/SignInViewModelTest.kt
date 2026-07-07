/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.ui.feature.signin

import com.oztechan.adtrack.data.auth.browser.AuthCancelledException
import com.oztechan.adtrack.fakes.FakeAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SignInViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun successful_sign_in_emits_navigate_to_dashboard() = runTest(dispatcher) {
        val viewModel = SignInViewModel(FakeAuthRepository(signInResult = Result.success(Unit)))
        val effects = mutableListOf<SignInEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.effect.collect(effects::add) }

        viewModel.event.onSignInClick()
        advanceUntilIdle()

        assertEquals(SignInEffect.NavigateToDashboard, effects.single())
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun failed_sign_in_emits_error() = runTest(dispatcher) {
        val viewModel = SignInViewModel(FakeAuthRepository(signInResult = Result.failure(RuntimeException("boom"))))
        val effects = mutableListOf<SignInEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.effect.collect(effects::add) }

        viewModel.event.onSignInClick()
        advanceUntilIdle()

        assertTrue(effects.any { it is SignInEffect.ShowError })
    }

    @Test
    fun cancellation_does_not_emit_error() = runTest(dispatcher) {
        val viewModel = SignInViewModel(FakeAuthRepository(signInResult = Result.failure(AuthCancelledException())))
        val effects = mutableListOf<SignInEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.effect.collect(effects::add) }

        viewModel.event.onSignInClick()
        advanceUntilIdle()

        assertTrue(effects.isEmpty())
    }
}
