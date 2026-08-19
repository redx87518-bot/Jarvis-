plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    compileSdk = 35

    namespace = "com.jarvis.data"

    defaultConfig {
        minSdk = 24
        targetSdk = 35
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    api(project(":core"))

    api(libs.androidxRoomRuntime)
    api(libs.androidxRoomKtx)
    ksp(libs.androidxRoomCompiler)

    api(libs.kotlinxSerializationJson)
    api(libs.kotlinxCoroutinesAndroid)

    api("androidx.datastore:datastore-preferences:1.1.0")
    api("androidx.datastore:datastore:1.0.0")

    api(libs.hiltAndroid)
    kapt(libs.hiltCompiler)

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("app.cash.turbine:turbine:1.1.0")
    testImplementation("com.google.truth:truth:1.4.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
