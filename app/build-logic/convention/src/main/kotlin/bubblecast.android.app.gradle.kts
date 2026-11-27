// Сборка приложения: только app/.
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = bubblecastNamespace
    compileSdk = version("android-compileSdk").toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(version("jvm"))
        targetCompatibility = JavaVersion.toVersion(version("jvm"))
    }
    defaultConfig {
        applicationId = "com.npcknows.bubblecast"
        minSdk = version("android-minSdk").toInt()
        targetSdk = version("android-targetSdk").toInt()
        versionCode = 1
        versionName = "0.1.0"
    }
    buildFeatures { compose = true }
}
configureJvmTarget()
addModuleDefaults()
dependencies {
    "implementation"(platform(lib("compose-bom")))
    "implementation"(lib("compose-ui"))
    "implementation"(lib("compose-material3"))
    "implementation"(lib("activity-compose"))
}
