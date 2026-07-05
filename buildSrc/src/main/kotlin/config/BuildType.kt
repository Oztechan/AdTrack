/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */
package config

enum class BuildType {
    DEBUG,
    RELEASE;

    companion object {
        val debug = DEBUG.name.lowercase()
        val release = RELEASE.name.lowercase()
    }
}
