plugins { id("bubblecast.android") }

// Совпадает с колонкой «Зависит от» в arch.md (shared добавляет build-logic).
dependencies {
    implementation(project(":modules:audio_session"))
}
