// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    // Compose Compiler plugin required when Compose is enabled (Kotlin 1.9.25+)
    alias(libs.plugins.compose.compiler) apply false
    // KSP and Hilt must be in same scope to avoid class loader issues
    // See: https://github.com/google/dagger/issues/3965
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
}