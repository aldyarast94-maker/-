// Модуль-библиотека Android с экранами на Compose.
plugins {
    id("bubblecast.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android { buildFeatures { compose = true } }
dependencies {
    "implementation"(platform(lib("compose-bom")))
    "implementation"(lib("compose-ui"))
    "implementation"(lib("compose-material3"))
}
