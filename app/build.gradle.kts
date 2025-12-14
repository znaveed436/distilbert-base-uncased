import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.hook.automation"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.hook.automation"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug { isDebuggable = true }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = libs.versions.kotlin.get() }

    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.material)
    implementation(libs.recyclerview)

    implementation(libs.yukihookapi.api)
    implementation(libs.kavaref.core)
    compileOnly(libs.xposed.api)
    ksp(libs.yukihookapi.ksp.xposed)

    debugImplementation(libs.compose.ui.tooling)
}

ksp {
    arg("YUKIHOOKAPI_PACKAGE_NAME", "com.hook.automation")
}