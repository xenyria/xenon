plugins {
    kotlin("jvm") version "2.2.10"
    id("maven-publish")
}

println("Publishing as " + getProperty("PXGD_PUBLIC_USERNAME"))

fun getProperty(key: String): String? {
    var property = findProperty(key)
    if (property == null) property = System.getenv(key)
    return property as? String
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

java {
    withSourcesJar()
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
            name = "PixelgroundLabs"
            url = uri("https://maven.pixelgroundlabs.com/releases")
            credentials {
                username = getProperty("PXGD_PUBLIC_USERNAME")
                password = getProperty("PXGD_PUBLIC_PASSWORD")
            }
        }
    }
}
