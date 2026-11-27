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

// В тестах на компьютере Android-сборка ONNX Runtime не загрузится — подменяем на настольную с тем же API.
// Нужна всем модулям: ONNX приходит и транзитивно (pipeline → tts).
dependencies { "testImplementation"(lib("onnxruntime-desktop")) }
configurations.matching { it.name.endsWith("UnitTestRuntimeClasspath") }.configureEach {
    exclude(group = "com.microsoft.onnxruntime", module = "onnxruntime-android")
}
