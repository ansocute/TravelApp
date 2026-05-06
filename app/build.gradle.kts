plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    alias(libs.plugins.kotlin.android) // Dùng alias để khớp với bản 1.9.22 hoặc 2.x trong file TOML
    id("kotlin-kapt")
}

android {
    namespace = "com.nhom.travelapp"
    compileSdk = 34 // Đưa về 34 để ổn định hơn với các thư viện hiện tại

    defaultConfig {
        applicationId = "com.nhom.travelapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["MAPS_API_KEY"] = "AIzaSyCwWG45OxI0AZ04KqfjW_Ei45r237VbhXE"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        // Quan trọng: Thêm cái này để hỗ trợ kapt xử lý metadata tốt hơn
        freeCompilerArgs += listOf("-Xjvm-default=all", "-Xno-param-assertions")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    // --- FIX LỖI METADATA (ĐƯA LÊN ĐẦU) ---
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.9.0")
    kapt("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.9.0")

    // --- ROOM DATABASE (Bản 2.6.1) ---
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    // --- GOOGLE SERVICES & LOCATION ---
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation(libs.play.services.maps)

    // --- FIREBASE ---
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore")

    // --- UI & IMAGE LOADING ---
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // --- ANDROIDX & LIFECYCLE (Dùng từ TOML để đồng bộ) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // --- COROUTINES ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    // --- NAVIGATION ---
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.legacy.support.v4)

    // --- TESTING ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

kapt {
    correctErrorTypes = true
}