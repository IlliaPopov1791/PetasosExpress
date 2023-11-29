plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "ca.hermeslogistics.itservices.petasosexpress"
    compileSdk = 33

    defaultConfig {
        applicationId = "ca.hermeslogistics.itservices.petasosexpress"
        minSdk = 28
        targetSdk = 33
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
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Firebase Dependencies
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database:20.0.1")
    implementation("com.google.firebase:firebase-storage:20.0.1")
    implementation ("com.firebaseui:firebase-ui-storage:8.0.0")
    implementation("com.google.firebase:firebase-auth:22.1.2")
    implementation("com.google.firebase:firebase-firestore:24.0.0")
    implementation("androidx.test.ext:junit:1.1.5")

    // Unit Test Dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:3.3.3")

    // Robolectric
    testImplementation("org.robolectric:robolectric:4.11.1")

    // AndroidX Test libraries
    testImplementation("androidx.test:core:1.4.0") // Check for the latest version
    testImplementation("androidx.fragment:fragment-testing:1.3.6") // Check for the latest version

    // Android Test Dependencies
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-accessibility:3.5.1")
    androidTestImplementation ("androidx.fragment:fragment-testing:1.4.0")
    testImplementation ("org.junit.jupiter:junit-jupiter-api:5.8.0")
    testImplementation ("org.junit.jupiter:junit-jupiter-engine:5.8.0")

}
