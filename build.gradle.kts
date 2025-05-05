// Корневой build.gradle.kts — для настройки всех подпроектов (без зависимостей)
plugins {
    kotlin("jvm") version "1.9.22" apply false
    id("org.jetbrains.compose") version "1.6.0" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
