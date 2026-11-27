// Модуль-библиотека Android.
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = bubblecastNamespace
    compileSdk = version("android-compileSdk").toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(version("jvm"))
        targetCompatibility = JavaVersion.toVersion(version("jvm"))
    }
    defaultConfig { minSdk = version("android-minSdk").toInt() }
}
configureJvmTarget()
addModuleDefaults()
dependencies { "implementation"(lib("kotlinx-coroutines-android")) }
