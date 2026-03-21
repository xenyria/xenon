plugins {
    kotlin("jvm") version "2.3.0"
    id("maven-publish")
}

group = "net.xenyria.xenon"
version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    api(project(":core"))
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