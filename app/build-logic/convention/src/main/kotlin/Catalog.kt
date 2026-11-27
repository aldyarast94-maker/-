import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/** Доступ к gradle/libs.versions.toml из convention-плагинов. */
internal val Project.catalog: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.version(alias: String): String = catalog.findVersion(alias).get().requiredVersion

internal fun Project.lib(alias: String) = catalog.findLibrary(alias).get()

/** Пространство имён модуля: com.npcknows.bubblecast.<имя папки>. */
internal val Project.bubblecastNamespace: String
    get() = if (path == ":app") "com.npcknows.bubblecast" else "com.npcknows.bubblecast.$name"

/** Общие зависимости любого модуля карты: shared и корутины; настройки тестов. */
internal fun Project.addModuleDefaults() {
    // Тесты с настоящими моделями: папка из BUBBLECAST_MODELS (без неё такие тесты пропускаются).
    tasks.withType(org.gradle.api.tasks.testing.Test::class.java).configureEach {
        System.getenv("BUBBLECAST_MODELS")?.let { environment("BUBBLECAST_MODELS", it) }
        maxHeapSize = "4g"
    }
    dependencies.add("implementation", lib("kotlinx-coroutines-core"))
    dependencies.add("testImplementation", lib("junit"))
    if (path != ":shared") {
        dependencies.add("implementation", dependencies.project(mapOf("path" to ":shared")))
        dependencies.add("testImplementation", dependencies.testFixtures(dependencies.project(mapOf("path" to ":shared"))))
    }
}

/** Байткод под Java из libs.versions.toml (jvm), компилирует любой установленный JDK не ниже. */
internal fun Project.configureJvmTarget() {
    val target = version("jvm")
    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).configureEach {
        compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(target))
    }
    tasks.withType(org.gradle.api.tasks.compile.JavaCompile::class.java).configureEach {
        sourceCompatibility = target
        targetCompatibility = target
    }
}
