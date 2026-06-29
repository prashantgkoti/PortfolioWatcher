plugins {
    // android.application (AGP 9.2.1) has built-in Kotlin support
    // Don't apply kotlin.android separately - it causes conflict
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.example.portfoliowatcher"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.portfoliowatcher"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // ===== CORE =====
    implementation(libs.androidx.core.ktx)

    // ===== JETPACK COMPOSE (UI & Material Design) =====
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.foundation)

    // ===== NAVIGATION (Screen switching) =====
    implementation(libs.androidx.navigation.compose)

    // ===== VIEWMODEL & LIFECYCLE (State Management) =====
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // ===== COROUTINES (Async tasks) =====
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // ===== HILT (Dependency Injection) =====
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

    // ===== ROOM (Local Database) =====
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // ===== FIREBASE (Auth & Cloud Firestore) =====
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)

    // ===== NETWORKING (Retrofit, OkHttp, Serialization) =====
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.kotlinx.serialization.converter)

    // ===== IMAGE LOADING =====
    implementation(libs.coil.compose)

    // ===== ENCRYPTION =====
    implementation(libs.tink.android)
    implementation(libs.androidx.security.crypto)

    // ===== TESTING =====
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // ===== DEBUG =====
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}