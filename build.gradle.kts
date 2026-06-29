// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
    // Hilt removed - incompatible with AGP 9.2.1 (BaseExtension not found)
    // Add back when Hilt releases AGP 9.x compatible version
    // alias(libs.plugins.hilt.android) apply false
}
