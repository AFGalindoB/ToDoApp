import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    kotlin("plugin.serialization") version "2.0.0"
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "com.afgalindob.assistantapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.afgalindob.assistantapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file("keystore.properties")
            if (keystorePropertiesFile.exists()) {
                val properties = Properties()
                keystorePropertiesFile.inputStream().use { properties.load(it) }
                
                val storeFilePath = properties.getProperty("MYAPP_RELEASE_STORE_FILE")
                val keystoreFile = if (storeFilePath != null) rootProject.file(storeFilePath) else null

                if (keystoreFile != null && keystoreFile.exists()) {
                    storeFile = keystoreFile
                    storePassword = properties.getProperty("MYAPP_RELEASE_STORE_PASSWORD")
                    keyAlias = properties.getProperty("MYAPP_RELEASE_KEY_ALIAS")
                    keyPassword = properties.getProperty("MYAPP_RELEASE_KEY_PASSWORD")
                    println("INFO: Usando llave de producción oficial.")
                } else {
                    initWith(getByName("debug"))
                    println("WARNING: Keystore file '${storeFilePath}' no encontrado en la raíz. Usando llave de debug.")
                }
            } else {
                initWith(getByName("debug"))
                println("WARNING: keystore.properties no encontrado. Usando llave de debug.")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true // Activa R8 para optimizar y reducir tamaño
            signingConfig = signingConfigs.getByName("release")
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
    buildFeatures {
        compose = true
    }
}

baselineProfile {
    // Guarda los perfiles generados en src/main/generated/baselineProfiles
    saveInSrc = true
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation(libs.androidx.navigation.compose)

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation(libs.material3)
    implementation(libs.androidx.compose.animation)
    implementation(libs.androidx.runtime)
    "baselineProfile"(project(":baselineprofile"))
    ksp("androidx.room:room-compiler:2.6.1")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation(libs.androidx.compose.runtime)

    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.profileinstaller:profileinstaller:1.4.1")
}