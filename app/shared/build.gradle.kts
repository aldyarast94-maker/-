plugins {
    id("bubblecast.kotlin")
    id("bubblecast.serialization")
    `java-test-fixtures`
}

// Типы контрактов генерируются из contracts/ (tools/gen_contracts.py) сюда.
sourceSets.main { kotlin.srcDir("contracts_gen") }

dependencies {
    // Типы shared видны в публичных входах модулей — отдаём их зависимости дальше.
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.serialization.json)
}

// Помощники для тестов всех модулей (папка моделей и т. п.) — src/testFixtures, одно место.
dependencies { testFixturesImplementation(libs.junit) }
