// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false

    alias(libs.plugins.compose.compiler) apply false

    id("com.google.devtools.ksp") version "2.0.10-1.0.24" apply false

    id("com.google.protobuf") version "0.9.4" apply false

    id("com.google.gms.google-services") version "4.4.2" apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false

    id("com.google.dagger.hilt.android") version "2.51.1" apply false
}