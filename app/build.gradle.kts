plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.recyclar"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.recyclar"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    //noinspection UseTomlInstead
    implementation ("androidx.recyclerview:recyclerview:1.3.0")
    implementation("com.haibin:calendarview:3.7.1")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation (libs.appcompat.v7)
    implementation (libs.design)
    implementation (libs.floatingactionbutton)
    //noinspection GradleDependency
    implementation ("com.android.support:design:26.0.0-alpha")
    implementation ("com.google.android.material:material:1.12.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.preference)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}