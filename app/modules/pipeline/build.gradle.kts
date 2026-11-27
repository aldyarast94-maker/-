plugins { id("bubblecast.android") }

// Совпадает с колонкой «Зависит от» в arch.md (shared добавляет build-logic).
dependencies {
    implementation(project(":modules:script"))
    implementation(project(":modules:characters"))
    implementation(project(":modules:cast"))
    implementation(project(":modules:text_prep"))
    implementation(project(":modules:tts"))
}
