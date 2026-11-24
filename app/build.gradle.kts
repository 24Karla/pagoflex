import java.net.URI
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val localProperties = Properties().apply {
    rootProject.file("local.properties").takeIf { it.isFile }?.inputStream()?.use { load(it) }
}
val gatewayUrl = URI(
    providers.gradleProperty("pagoflex.gatewayUrl").orNull
        ?: localProperties.getProperty("pagoflex.gatewayUrl")
        ?: "http://10.0.2.2:8080/"
)
require(
    gatewayUrl.scheme in listOf("http", "https") && gatewayUrl.host != null &&
        gatewayUrl.userInfo == null && gatewayUrl.rawQuery == null && gatewayUrl.rawFragment == null
) { "pagoflex.gatewayUrl debe ser una URL HTTP o HTTPS sin credenciales, consulta ni fragmento." }

android {
    namespace = "com.example.pagoflex"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.pagoflex"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "GATEWAY_URL", "\"${gatewayUrl.toASCIIString().trimEnd('/')}/\"")
        manifestPlaceholders["pagoflexCleartext"] = "false"
    }

    buildTypes {
        debug {
            manifestPlaceholders["pagoflexCleartext"] = (gatewayUrl.scheme == "http").toString()
        }
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

configurations.all {
    resolutionStrategy {
        force("androidx.annotation:annotation-experimental:1.4.1")
    }
}

dependencies {
    implementation(libs.androidx.annotation.experimental)
    implementation("androidx.navigation:navigation-compose:2.9.6")
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.11.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.11.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
}
