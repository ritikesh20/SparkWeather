plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.sparkweather"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.sparkweather"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.work:work-runtime:2.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    implementation("com.google.android.material:material:1.11.0")

    implementation("com.google.android.gms:play-services-location:21.1.0")

    implementation("com.mikepenz:fastadapter:3.3.1") // working
    implementation("com.mikepenz:fastadapter-extensions-expandable:3.3.1")
    implementation("com.mikepenz:fastadapter-extensions:3.3.1") // working

    implementation("com.mikepenz:fastadapter-commons:3.3.1") // working

//    implementation("androidx.work:work-runtime:2.9.0")
    implementation("androidx.core:core:1.12.0")

    implementation("com.airbnb.android:lottie:6.0.0")

//    implementation ("com.google.android.material:material:1.11.0")

}