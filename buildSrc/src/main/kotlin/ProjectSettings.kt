/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */
import org.gradle.api.JavaVersion
import org.gradle.api.Project

object ProjectSettings {

    private const val MAYOR_VERSION = 0
    private const val MINOR_VERSION = 0

    const val PROJECT_ID = "com.oztechan.adtrace"

    const val COMPILE_SDK_VERSION = 36
    const val MIN_SDK_VERSION = 24
    const val TARGET_SDK_VERSION = 36

    val JAVA_VERSION = JavaVersion.VERSION_21

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    fun getVersionCode(project: Project): Int = try {
        gitCommitCount(project).toInt()
    } catch (e: Exception) {
        1
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    fun getVersionName(project: Project): String = try {
        "$MAYOR_VERSION.$MINOR_VERSION.${getVersionCode(project)}"
    } catch (e: Exception) {
        "$MAYOR_VERSION.$MINOR_VERSION.0"
    }

    private fun gitCommitCount(project: Project): String = project.providers.exec {
        commandLine("git rev-list --first-parent --count HEAD".split(" "))
    }.standardOutput.asText.get().trim()
}
