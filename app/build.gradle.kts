import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.gms.google-services")
}

val properties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

android {
    namespace = "com.credential.cubrism"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.credential.cubrism"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String","SPRING_URL", getApi("SPRING_URL"))
        buildConfigField("String","GOOGLE_CLIENT_ID", getApi("GOOGLE_CLIENT_ID"))
        buildConfigField("String","KAKAO_NATIVE_APP_KEY", getApi("KAKAO_NATIVE_APP_KEY"))
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = properties["KAKAO_NATIVE_APP_KEY"].toString().replace("\"", "")
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
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

fun getApi(propertyKey: String): String {
    val propertiesFile = rootProject.file("local.properties")
    val properties = Properties().apply {
        load(FileInputStream(propertiesFile))
    }
    return properties.getProperty(propertyKey)
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation("com.google.firebase:firebase-analytics")
    // androidx
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // google
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.gms:play-services-auth:21.1.0")
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.10.0")
    implementation("com.squareup.retrofit2:converter-gson:2.10.0")

    // 기타
    implementation("com.kakao.sdk:v2-user:2.20.1")
    implementation("com.airbnb.android:lottie:6.3.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.etebarian:meow-bottom-navigation:1.2.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.tickaroo.tikxml:retrofit-converter:0.9.0_11-SNAPSHOT")
    implementation("com.github.skydoves:powermenu:2.2.4")
    implementation("com.vanniktech:android-image-cropper:4.5.0")
    implementation("io.github.ParkSangGwon:tedimagepicker:1.5.0")
    implementation("com.google.firebase:firebase-messaging")

    // 테스트
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}