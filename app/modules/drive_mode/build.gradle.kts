plugins { id("bubblecast.android.ui") }

// Совпадает с колонкой «Зависит от» в arch.md (shared добавляет build-logic).
dependencies {
    implementation(project(":modules:reading_session"))
}
