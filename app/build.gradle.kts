import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists() && localPropertiesFile.isFile) {
    FileInputStream(localPropertiesFile).use { localProperties.load(it) }
}

android {
    namespace = "com.aokaze.anima"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aokaze.anima"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        val supabaseUrl = localProperties.getProperty("SUPABASE_URL", "")
        val supabaseAnonKey = localProperties.getProperty("SUPABASE_ANON_KEY", "")

        if (supabaseUrl.isBlank() || supabaseAnonKey.isBlank()) {
            println("Warning: SUPABASE_URL or SUPABASE_ANON_KEY not found in local.properties. BuildConfig fields will be empty.")
        }

        buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui.tooling.preview)

    // Material Design Icons
    implementation(libs.androidx.material.icons.extended)

    // Material Design TV
    implementation(libs.androidx.tv.material)

    // Material Design 3
    implementation(libs.androidx.material3.android)

    // Compose Navigation
    implementation(libs.androidx.navigation.compose)

    // Coil
    implementation(libs.coil.compose)

    // Exoplayer
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui.compose)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.exoplayer.hls)

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
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // Compose Previews
    debugImplementation(libs.androidx.compose.ui.tooling)

    // SplashScreen
    implementation(libs.androidx.core.splashscreen)
}