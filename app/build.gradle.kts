plugins {
    kotlin("plugin.serialization") version "2.0.21"
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.cartao"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.cartao"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.foundation)
    // Definindo Versões
    val nav_version = "2.9.4"
    val room_version = "2.7.0-alpha03"
    val retrofit_version = "2.11.0"
    val coroutines_version = "1.8.1"
    val lifecycle_version = "2.8.3"
    val kts_version = "1.7.3"
    // Versão OkHttp, que é a base do Retrofit
    val okhttp_version = "4.12.0"

    // --- Compose e Core (Dependências Básicas) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // --- Navegação Compose ---
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // --- ROOM para Persistência Local ---
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    ksp("androidx.room:room-compiler:$room_version")

    // --- RETROFIT, KTS e OKHTTP ---
    implementation("com.squareup.retrofit2:retrofit:$retrofit_version")

    // Conversor Kotlinx Serialization para Retrofit
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    // KTS: Biblioteca JSON principal
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kts_version")

    // Adiciona o módulo BOM do OkHttp
    implementation(platform("com.squareup.okhttp3:okhttp-bom:$okhttp_version"))
    // Dependência do módulo de extensões do Kotlin para OkHttp (necessário para 'toMediaType')
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor") // Opcional, mas útil para debug

    // --- COROUTINES e ViewModel ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")

    // --- Testes ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}