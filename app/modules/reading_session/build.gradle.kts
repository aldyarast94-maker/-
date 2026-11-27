plugins { id("bubblecast.android") }

// Совпадает с колонкой «Зависит от» в arch.md (shared добавляет build-logic).
dependencies {
    implementation(project(":modules:reader"))
    implementation(project(":modules:pipeline"))
    implementation(project(":modules:narration"))
    implementation(project(":modules:overlay"))
    implementation(project(":modules:library"))
}
