plugins {
    autowire(libs.plugins.android.application)
    autowire(libs.plugins.kotlin.android)
    autowire(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.recycle.twitter"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.recycle.twitter"
        minSdk = 27
        targetSdk = 34
        // 1.2.3 translates to 1_002_003_0. The last digit is reserved.
        // Note that versionCode <= 210_000_000_0
        versionCode = 1_000_000_00
        // Versioning strategy: In a.b.c,
        // a is for breaking change,
        // b is for new feature or change,
        // c is for bug fix
        versionName = "1.0.0"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    }
    androidResources.additionalParameters += listOf(
        "--allow-reserved-package-id",
        "--package-id",
        "0x4f"
    )
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.preference:preference-ktx:1.2.1")

    compileOnly(de.robv.android.xposed.api)
    implementation(com.highcapable.yukihookapi.api)
    ksp(com.highcapable.yukihookapi.ksp.xposed)

    implementation("org.luckypray:dexkit:2.0.2")
//    implementation("com.tencent:mmkv:1.3.5")
}