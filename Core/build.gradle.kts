plugins {
    kotlin("jvm") version "2.2.10"
    id("maven-publish")
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenLocal()
    mavenCentral()
}

group = "net.xenyria.xenon"
version = "1.0.0"

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.json:json:20250517")
    implementation("net.openhft:zero-allocation-hashing:0.16")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = "core"
            version = project.version.toString()
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/xenyria/xenon")
            credentials {
                username = findProperty("gpr.user") as? String ?: System.getenv("GH_USERNAME")
                password = findProperty("gpr.token") as? String ?: System.getenv("GH_PACKAGES_TOKEN")
            }
        }
    }
}