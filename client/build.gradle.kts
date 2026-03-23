plugins {
    kotlin("jvm") version "2.3.0"
    id("maven-publish")
    id("com.gradleup.shadow") version "9.1.0"
}

group = "net.xenyria.xenon"
version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    testImplementation(kotlin("test"))
    api(project(":core"))

    api("com.github.JnCrMx:discord-game-sdk4j:v1.0.0")
    shadow("com.github.JnCrMx:discord-game-sdk4j:v1.0.0")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = "client"
            version = project.version.toString()
        }
    }
}