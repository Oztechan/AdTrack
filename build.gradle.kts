/*
 * Copyright (c) 2026 Mustafa Ozhan. All rights reserved.
 */
import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    libs.plugins.apply {
        alias(kotlinMultiplatform).apply(false)
        alias(kotlinAndroid).apply(false)
        alias(jetbrainsCompose).apply(false)
        alias(kotlinPluginCompose).apply(false)
        alias(androidApplication).apply(false)
        alias(androidLibrary).apply(false)
        alias(serialization).apply(false)
        alias(buildKonfig).apply(false)
        alias(mokkery).apply(false)
        alias(googleServices).apply(false)
        alias(detekt)
    }
}

group = ProjectSettings.PROJECT_ID
version = ProjectSettings.getVersionName(project)

allprojects {
    apply(plugin = rootProject.libs.plugins.detekt.get().pluginId).also {
        detekt {
            buildUponDefaultConfig = true
            allRules = true
            parallel = true
            config.from(rootProject.layout.projectDirectory.file("detekt.yml"))
        }

        tasks.withType<Detekt> {
            val projectDirectory = layout.projectDirectory.asFile
            val buildDirectory = layout.buildDirectory.asFile

            setSource(projectDirectory)
            exclude("**/build/**")
            exclude {
                val relativePath = it.file.relativeTo(projectDirectory)
                relativePath.startsWith(buildDirectory.get().relativeTo(projectDirectory))
            }
        }

        tasks.register("detektAll") {
            dependsOn(
                tasks.withType<Detekt>().matching {
                    !it.name.startsWith("detektAndroid")
                }
            )
        }

        dependencies {
            detektPlugins(rootProject.libs.common.detektFormatting)
        }
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.add("-Xexpect-actual-classes")
            allWarningsAsErrors = true
        }
    }
}
