plugins {
    kotlin("jvm") version "2.3.0"
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

val exposedVersion = "1.3.0"

dependencies {
    testImplementation(kotlin("test"))

    implementation("io.javalin:javalin:6.7.0")
    implementation("io.javalin:javalin-rendering:6.7.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")

    implementation("gg.jte:jte:3.2.4")
    implementation("gg.jte:jte-kotlin:3.2.4")

    implementation("org.slf4j:slf4j-simple:2.0.16")

    implementation("io.ktor:ktor-client-core:3.2.2")
    implementation("io.ktor:ktor-client-cio:3.2.2")
    implementation("io.ktor:ktor-client-content-negotiation:3.2.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.2")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-migration-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-migration-jdbc:$exposedVersion")
    implementation("org.xerial:sqlite-jdbc:3.49.1.0")

    implementation("com.fleeksoft.ksoup:ksoup:0.2.5")

    implementation("com.github.ajalt.clikt:clikt:5.1.0")
}

tasks.test {
    useJUnitPlatform()
}

val copyStaticWebResources by tasks.registering(Copy::class) {
    from(layout.projectDirectory.dir("src/main/resources/static"))
    into(layout.projectDirectory.dir("static"))
}
tasks.findByName("processResources")!!.dependsOn(copyStaticWebResources)

val cleanStaticWebResources by tasks.registering(Delete::class) {
    delete(layout.projectDirectory.dir("static"))
}
tasks.findByName("clean")!!.dependsOn(cleanStaticWebResources)