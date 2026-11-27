// Добавка к Android-модулю, который запускает модели ONNX (script, tts).
// На телефоне — onnxruntime-android; в тестах на компьютере — настольная сборка с тем же API ai.onnxruntime.
dependencies {
    "implementation"(lib("onnxruntime-android"))
    "testImplementation"(lib("onnxruntime-desktop"))
}

configurations.matching { it.name.endsWith("UnitTestRuntimeClasspath") }.configureEach {
    exclude(group = "com.microsoft.onnxruntime", module = "onnxruntime-android")
}

// Тесты с настоящими моделями: папка моделей из переменной BUBBLECAST_MODELS (без неё такие тесты пропускаются).
tasks.withType(Test::class.java).configureEach {
    System.getenv("BUBBLECAST_MODELS")?.let { environment("BUBBLECAST_MODELS", it) }
    maxHeapSize = "4g"
}
