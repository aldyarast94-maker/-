// Добавка к модулю, которому нужен kotlinx.serialization (shared — для контрактов).
plugins { id("org.jetbrains.kotlin.plugin.serialization") }

dependencies { "implementation"(lib("kotlinx-serialization-json")) }
