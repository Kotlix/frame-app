pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("jvm") version "1.9.22"  // Используем более новую версию Kotlin
        id("org.jetbrains.compose") version "1.5.10"  // Обновляем Compose до 1.5.10 или выше
    }
}


dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "frame-app"
include(":app")
include(":core")
include(":auth")
include(":home")
