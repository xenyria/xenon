plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
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
    jvmToolchain(25)
}

repositories {
    mavenLocal()
    mavenCentral()
}

group = "net.xenyria.xenon"
version = "1.1.0"

dependencies {
    testImplementation(kotlin("test"))
    implementation(libs.org.json)
    implementation(libs.openhft.zero.allocation.hashing)
    implementation(libs.org.joml)
    implementation(libs.kotlin.serialization.json)
}

java {
    withSourcesJar()
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(25)
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
