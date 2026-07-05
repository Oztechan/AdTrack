/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */
package config.key

import getSecret
import org.gradle.api.Project

fun Project.secret(key: Key) = getSecret(
    key = key.name,
    default = key.default
)
