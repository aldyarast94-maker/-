plugins {
    id("bubblecast.android")
    id("bubblecast.onnx")
}

// Совпадает с колонкой «Зависит от» в arch.md (shared добавляет build-logic).
dependencies {
    implementation(project(":modules:api_client"))
}
