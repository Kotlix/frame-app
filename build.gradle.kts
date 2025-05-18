import java.net.URI

// Корневой build.gradle.kts — для настройки всех подпроектов (без зависимостей)
plugins {
    kotlin("jvm") version "1.9.22" apply false
    id("org.jetbrains.compose") version "1.6.0" apply false
    id("com.google.protobuf") version "0.9.5" apply false
}

fun RepositoryHandler.kotlix(repo: String) = maven {
    name = "GitHubPackages"
    url = URI.create("https://maven.pkg.github.com/Kotlix/$repo")
    credentials {
        // picks from: .../user/.gradle/gradle.properties
        username = System.getenv("GITHUB_ACTOR") ?: findProperty("GITHUB_LOGIN") as String?
        password = System.getenv("GITHUB_TOKEN") ?: findProperty("GITHUB_TOKEN") as String?
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        kotlix("frame-gateway")
        kotlix("frame-session")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

