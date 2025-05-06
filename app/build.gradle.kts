import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
}

FileInputStream(rootProject.file("local.properties")).use { Properties().load(it) }

android {
    namespace = "com.aokaze.anima"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aokaze.anima"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "SUPABASE_URL", Properties().getProperty("SUPABASE_URL", ""))
        buildConfigField("String", "SUPABASE_ANON_KEY", Properties().getProperty("SUPABASE_ANON_KEY", ""))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)

    // JSON parser
    implementation(libs.kotlinx.serialization)

    // Paging
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    // Supabase
    implementation(libs.postgrest.kt)
    implementation(libs.ktor.client.android)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.compiler)
}