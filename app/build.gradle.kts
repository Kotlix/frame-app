import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "ru.kotlix"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
   // implementation(platform("org.jetbrains.compose:compose-bom:2023.03.00"))
    implementation(compose.desktop.currentOs)

    implementation(project(":core"))
    implementation(project(":auth"))
    implementation(project(":home"))

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("org.springframework.boot:spring-boot-starter-web:2.7.0")

    implementation("io.insert-koin:koin-core:3.5.3")
    implementation("io.insert-koin:koin-compose:1.0.4")
}

compose.desktop {
    application {
        mainClass = "app.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "app"
            packageVersion = "1.0.0"
        }
    }
}
