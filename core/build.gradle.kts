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
    //implementation(platform("org.jetbrains.compose:compose-bom:2023.06.00")) // Попробуй более новую версию
    //implementation("org.jetbrains.compose.desktop:desktop-common")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("org.springframework.boot:spring-boot-starter-web:2.7.0")

    implementation("io.insert-koin:koin-core:3.5.3")
    implementation("io.insert-koin:koin-compose:1.0.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
}

//import org.jetbrains.compose.desktop.application.dsl.TargetFormat
//
//plugins {
//    kotlin("jvm")
//    id("org.jetbrains.compose")
//}
//
//group = "ru.kotlix"
//version = "1.0-SNAPSHOT"
//
//repositories {
//    mavenCentral()
//    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
//    google()
//}
//
//dependencies {
//    // Note, if you develop a library, you should use compose.desktop.common.
//    // compose.desktop.currentOs should be used in launcher-sourceSet
//    // (in a separate module for demo project and in testMain).
//    // With compose.desktop.common you will also lose @Preview functionality
//    //implementation(compose.desktop.currentOs)
//}
//
//compose.desktop {
//    application {
//        mainClass = "MainKt"
//
//        nativeDistributions {
//            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
//            packageName = "core"
//            packageVersion = "1.0.0"
//        }
//    }
//}
