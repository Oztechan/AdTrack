/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */

package com.oztechan.adtrack.android.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

/**
 * Tracks the currently-resumed [Activity] behind a [WeakReference] so long-lived singletons (e.g. the
 * rewarded ad, which must be shown from an Activity) can reach it without leaking it. Register once
 * via [Application.registerActivityLifecycleCallbacks].
 */
class CurrentActivityHolder : Application.ActivityLifecycleCallbacks {

    private var activityRef: WeakReference<Activity> = WeakReference(null)

    val current: Activity? get() = activityRef.get()

    override fun onActivityResumed(activity: Activity) {
        activityRef = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        if (activityRef.get() === activity) activityRef.clear()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
    override fun onActivityDestroyed(activity: Activity) = Unit
}
