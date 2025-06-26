plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)

}

android {
    namespace = "com.example.knzwapp"
    compileSdk = 35
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.example.knzwapp"
        minSdk = 34
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "MAPS_API_KEY", "\"${project.properties["MAPS_API_KEY"]}\"")
        buildConfigField("String", "PLACES_API_KEY", "\"${project.properties["PLACES_API_KEY"]}\"")
        buildConfigField("String", "WEATHER_API_KEY", "\"${project.properties["WEATHER_API_KEY"]}\"")
        manifestPlaceholders["MAPS_API_KEY"] = project.properties["MAPS_API_KEY"].toString()
        manifestPlaceholders["PLACES_API_KEY"] = project.properties["PLACES_API_KEY"].toString()
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
    implementation(libs.firebase.database)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.play.services.location)
    implementation(libs.room.common.jvm)
    implementation(libs.room.runtime.android)
    implementation(libs.room.runtime)
    implementation(libs.play.services.maps)
    implementation(libs.places)
    implementation(libs.firebase.auth)
    implementation("com.google.firebase:firebase-storage:21.0.0")
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.loopj.android:android-async-http:1.4.9")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    annotationProcessor(libs.room.compiler)


}