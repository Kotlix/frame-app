import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "ru.kotlix"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)

    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Основная библиотека Retrofit
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Конвертер для Gson
    implementation("com.squareup.okhttp3:okhttp:4.9.3") // OkHttp клиент для Retrofit
    implementation("org.springframework.boot:spring-boot-starter-web:2.7.0") // Версия может измениться
    implementation("io.insert-koin:koin-core:3.5.3")
    implementation("io.insert-koin:koin-compose:1.0.4") // для Compose Desktop

}



