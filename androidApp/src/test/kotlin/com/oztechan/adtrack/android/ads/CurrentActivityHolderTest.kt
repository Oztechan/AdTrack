/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.android.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertNull
import kotlin.test.assertSame

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class)
class CurrentActivityHolderTest {

    private val holder = CurrentActivityHolder()
    private fun activity(): Activity = Robolectric.buildActivity(Activity::class.java).get()

    @Test
    fun tracks_the_resumed_activity_and_clears_on_pause() {
        assertNull(holder.current)

        val activity = activity()
        holder.onActivityResumed(activity)
        assertSame(activity, holder.current)

        holder.onActivityPaused(activity)
        assertNull(holder.current)
    }

    @Test
    fun pausing_a_different_activity_does_not_clear_the_current_one() {
        val resumed = activity()
        holder.onActivityResumed(resumed)

        holder.onActivityPaused(activity()) // some other activity paused
        assertSame(resumed, holder.current)
    }

    @Test
    fun lifecycle_no_ops_are_harmless() {
        val activity = activity()
        // These carry no state; exercised to document they are intentional no-ops.
        holder.onActivityCreated(activity, Bundle())
        holder.onActivityStarted(activity)
        holder.onActivityStopped(activity)
        holder.onActivitySaveInstanceState(activity, Bundle())
        holder.onActivityDestroyed(activity)
        assertNull(holder.current)
    }
}
