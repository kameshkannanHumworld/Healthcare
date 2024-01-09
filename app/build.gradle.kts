plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.healthcare"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.healthcare"
        minSdk = 23
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

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")  //swiperefresh layout
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.airbnb.android:lottie:3.4.0")                      // lottie
    implementation ("it.xabaras.android:recyclerview-swipedecorator:1.4")   //recycler view
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")                //retrofit
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")          //retrofit - gson
    implementation ("com.squareup.okhttp3:logging-interceptor:4.11.0")      //retrofit http
    implementation ("com.google.code.gson:gson:2.9.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1") //Location service
    implementation ("androidx.work:work-runtime:2.9.0")
    implementation("com.github.AtifSayings:Animatoo:1.0.1")
    implementation ("androidx.recyclerview:recyclerview-selection:1.1.0-rc01")





}