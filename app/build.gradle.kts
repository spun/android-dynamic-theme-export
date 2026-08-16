plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.screenshot)
}

android {
    namespace = "com.spundev.dynamicthemeexport"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.spundev.dynamicthemeexport"
        minSdk = 31
        targetSdk = 37
        versionCode = 5
        versionName = "0.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val storePath = System.getenv("SIGNING_STORE_PATH")
            storePath?.let {
                storeFile = file(storePath)
                storePassword = System.getenv("SIGNING_STORE_PASSWORD")
                keyAlias = System.getenv("SIGNING_KEY_ALIAS")
                keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            optimization {
                enable = true
            }
            signingConfig = signingConfigs.getByName("release")

            // Switch to debug signingConfigs to create release builds during development
            // signingConfig = signingConfigs.named("debug").get()
        }
    }

    testOptions {
        managedDevices {
            localDevices {
                create("pixel2api32") {
                    // Use device profiles you typically see in Android Studio.
                    device = "Pixel 2"
                    // Use only API levels 27 and higher (our app is >= 31, so no issues here).
                    // ATDs should support only API level 30, but this is working fine ¿?
                    apiLevel = 32
                    // To include Google services, use "google-atd".
                    systemImageSource = "aosp-atd"
                }
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

    experimentalProperties["android.experimental.enableScreenshotTest"] = true
}

dependencies {

    implementation(platform(libs.androidx.compose.bom))

    // Activity
    implementation(libs.androidx.activity.compose)
    // Compose
    implementation(libs.androidx.compose.material3)
    // Compose tooling
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // TESTS
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    // For local unit tests
    testImplementation(libs.junit)
    // For instrumentation tests
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    // For screenshot tests
    screenshotTestImplementation(libs.androidx.compose.ui.tooling)
    screenshotTestImplementation(libs.screenshot.validation.api)
}
