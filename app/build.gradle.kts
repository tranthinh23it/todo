plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.to_do_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.to_do_app"
        minSdk = 26
        targetSdk = 35
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
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    // Firebase App Check Debug provider
    implementation("com.google.firebase:firebase-appcheck-debug:17.0.0")
    implementation("com.google.firebase:firebase-messaging:24.0.0") // hoặc phiên bản mới nhất

    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.core.i18n)
    implementation(libs.play.services.base)
    implementation(libs.play.services.base)

    val nav_version = "2.9.1"

    implementation("androidx.navigation:navigation-compose:$nav_version")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Google Places
//    implementation(libs.places)

    // Gemini AI SDK - đúng cho Android
    implementation("com.google.ai.client.generativeai:generativeai:0.4.0")

    // Charts
    implementation("co.yml:ycharts:2.1.0")

    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // Icons
    implementation("androidx.compose.material:material-icons-extended:1.6.1")

    // ConstraintLayout in Compose
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
//    implementation(libs.androidx.media3.common.ktx)

    // Coil for images
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Date Picker
    implementation("io.github.vanpra.compose-material-dialogs:datetime:0.8.1-rc")

    // Unit (Dp, Sp...)
    implementation("androidx.compose.ui:ui-unit:1.6.4")

    // Material Design components
    implementation("com.google.android.material:material:1.11.0")

    // Activity Result API
    implementation("androidx.activity:activity-ktx:1.7.2")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("com.cloudinary:cloudinary-android:2.3.1")
    implementation("com.google.protobuf:protobuf-javalite:3.24.4")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    //snapper
//    implementation("com.google.accompanist:accompanist-pager:0.34.0") // hoặc version mới nhất
//    implementation("com.google.accompanist:accompanist-pager-indicators:0.34.0")
//    implementation("com.google.accompanist:accompanist-snapper:0.35.0")
    implementation("com.google.accompanist:accompanist-pager:0.34.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.34.0")
}

