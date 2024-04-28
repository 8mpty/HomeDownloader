import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.empty.homedownloader"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.empty.homedownloader"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val testurl : String = gradleLocalProperties(rootDir).getProperty("TEST_URL") ?: ""
        val myurl : String = gradleLocalProperties(rootDir).getProperty("MY_URL") ?: ""

        buildConfigField("String", "TEST_URL", "\"$testurl\"")
        buildConfigField("String", "MY_URL", "\"$myurl\"")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Custom/added implementations
    val okhttp3Ver = "4.12.0"
    implementation("com.squareup.okhttp3:okhttp:${okhttp3Ver}")

    val gsonVer = "2.10.1"
    implementation("com.google.code.gson:gson:${gsonVer}")

    val coroutinesVer = "1.6.4"
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:${coroutinesVer}")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}