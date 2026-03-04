// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
//    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.android.lint) apply false


    id("com.android.library") version "8.2.0" apply false
//    kotlin("multiplatform") version "1.9.22" apply false
    kotlin("plugin.serialization") version "2.2.20" apply false
//    id("org.jetbrains.compose") version "1.5.11" apply false
}