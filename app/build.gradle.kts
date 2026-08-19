plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlinCompose)
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    compileSdk = 35

    namespace = "com.jarvis.launcher"

    defaultConfig {
        applicationId = "com.jarvis.launcher"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0-alpha"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        manifestPlaceholders["applicationInfo.packageName"] = "com.jarvis.launcher"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
        unitTests.all {
            it.jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
        }
    }
}

afterEvaluate {
    tasks.matching { it.name == "hiltJavaCompileDebug" }.configureEach {
        doLast {
            val generatedDir = file("build/generated/hilt/component_sources/debug")
            val classOutput = file("build/intermediates/javac/debug/classes")
            val runtimeClasspath = configurations.findByName("debugRuntimeClasspath")
            if (generatedDir.exists() && generatedDir.listFiles()?.isNotEmpty() == true && runtimeClasspath != null) {
                val cp = (runtimeClasspath.files + classOutput).joinToString(File.pathSeparator) { it.absolutePath }
                exec {
                    commandLine(
                        "javac",
                        "-d", classOutput.absolutePath,
                        "-classpath", cp,
                        *generatedDir.walkTopDown().filter { it.extension == "java" }.map { it.absolutePath }.toTypedArray()
                    )
                }
            }
        }
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":data"))
    implementation(project(":android"))

    implementation(libs.androidxCoreKtx)
    implementation(libs.androidxActivityCompose)
    implementation(libs.androidxLifecycleRuntimeKtx)
    implementation(libs.androidxLifecycleViewmodelCompose)
    implementation(libs.androidxLifecycleViewmodelKtx)

    implementation(platform(libs.androidxComposeBom))
    implementation(libs.androidxComposeUi)
    implementation(libs.androidxComposeUiGraphics)
    implementation(libs.androidxComposeUiToolingPreview)
    implementation(libs.androidxComposeMaterial3)
    implementation(libs.androidxComposeMaterialIconsExtended)
    implementation(libs.androidxMaterial)
    implementation(libs.androidxNavigationCompose)
    implementation(libs.hiltNavigationCompose)

    implementation(libs.kotlinxCoroutinesCore)
    implementation(libs.kotlinxCoroutinesAndroid)
    implementation(libs.kotlinxSerializationJson)

    ksp(libs.androidxRoomCompiler)
    implementation(libs.androidxRoomRuntime)
    implementation(libs.androidxRoomKtx)

    implementation(libs.hiltAndroid)
    kapt(libs.hiltCompiler)

    debugImplementation(libs.androidxComposeUiTooling)

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("app.cash.turbine:turbine:1.1.0")
    testImplementation("com.google.truth:truth:1.4.2")
    testImplementation("io.mockk:mockk:1.13.12")
}
