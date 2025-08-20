plugins {
    kotlin("jvm") version "2.1.21"
    application
    id("org.jetbrains.kotlin.plugin.serialization") version("1.6.10")
}

group = "eden.movieq"
version = "1.0-SNAPSHOT"

application {
    mainClass = "eden.movieq.MainKt"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("io.javalin:javalin:6.7.0")
    implementation("io.javalin:javalin-rendering:6.7.0")

    implementation("gg.jte:jte:3.2.1")
    implementation("gg.jte:jte-kotlin:3.2.1")

    implementation("org.slf4j:slf4j-simple:2.0.16")

    implementation("io.ktor:ktor-client-core:3.2.2")
    implementation("io.ktor:ktor-client-cio:3.2.2")
    implementation("io.ktor:ktor-client-content-negotiation:3.2.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.2")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    implementation("org.jetbrains.exposed:exposed-core:1.0.0-beta-3")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.0.0-beta-3")
    implementation("org.jetbrains.exposed:exposed-dao:1.0.0-beta-3")
    implementation("org.jetbrains.exposed:exposed-java-time:1.0.0-beta-3")
    implementation("org.xerial:sqlite-jdbc:3.49.1.0")

    implementation("com.fleeksoft.ksoup:ksoup:0.2.5")
}

tasks.test {
    useJUnitPlatform()
}