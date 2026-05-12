plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.appdistribution)
}

android {
    namespace = "com.quoteday.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.romarudaze.quoteday"
        minSdk = 26
        targetSdk = 35
        versionCode = 7
        versionName = "1.4"
    }

    signingConfigs {
        create("release") {
            storeFile = file("quoteday-upload.jks")
            storePassword = "quoteday2024"
            keyAlias = "quoteday"
            keyPassword = "quoteday2024"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            firebaseAppDistribution {
                artifactType = "APK"
                releaseNotes = "Latest QuoteDay build"
                testers = "romarudazee99@gmail.com"
            }
        }
        debug {
            firebaseAppDistribution {
                artifactType = "APK"
                releaseNotes = "Debug build"
                testers = "romarudazee99@gmail.com"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.auth)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.core.splashscreen)
    implementation(libs.google.play.billing.ktx)
    debugImplementation(libs.androidx.ui.tooling)
}
