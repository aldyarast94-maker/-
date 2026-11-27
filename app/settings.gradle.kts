pluginManagement {
    includeBuild("build-logic")
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral() }
}

rootProject.name = "bubblecast"
include(":app", ":shared")
// Каждая папка в modules/ — модуль карты. Список не дублируем: берём папки.
file("modules").listFiles()!!.filter { it.isDirectory }.sorted().forEach { include(":modules:${it.name}") }
