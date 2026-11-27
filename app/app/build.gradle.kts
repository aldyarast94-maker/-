plugins { id("bubblecast.android.app") }

// Сборка знает все модули — только чтобы соединить их. Логики здесь нет.
dependencies {
    rootProject.file("modules").listFiles()!!.filter { it.isDirectory }.sorted()
        .forEach { implementation(project(":modules:${it.name}")) }
}
