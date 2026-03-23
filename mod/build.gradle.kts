/*
 * Copyright (c) 2026 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

import com.github.jengelman.gradle.plugins.shadow.ShadowBasePlugin.Companion.shadow
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("fabric-loom") version "1.15-SNAPSHOT"
    id("maven-publish")
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "9.1.0"
}

val minecraft_version: String by project
val loader_version: String by project
val fabric_kotlin_version: String by project
val fabric_api_version: String by project
val yacl_version: String by project

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 21
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
    maven { url = uri("https://jitpack.io") }
    maven("https://maven.isxander.dev/releases") {
        name = "Xander Maven"
    }
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${loader_version}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_api_version}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${fabric_kotlin_version}")
    modImplementation("dev.isxander:yet-another-config-lib:${yacl_version}")

    implementation(project(":core"))
    shadow(project(":core"))
    implementation(project(":client"))
    shadow(project(":client"))
    implementation("com.github.JnCrMx:discord-game-sdk4j:v1.0.0")
    shadow("com.github.JnCrMx:discord-game-sdk4j:v1.0.0")
}

tasks.processResources {
    inputs.property("version", project.version)
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.withType<JavaCompile>().configureEach {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    options.release = 21
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName}" }
    }
}

tasks.shadowJar {
    configurations = mutableListOf(project.configurations.shadow.get())
    exclude("META-INF")
}

tasks.remapJar {
    // wait until the shadowJar is done
    dependsOn(tasks.shadowJar.get())
    mustRunAfter(tasks.shadowJar.get())
    // Set the input jar for the task. Here use the shadow Jar that include the .class of the transitive dependency
    inputFile = tasks.shadowJar.get().archiveFile
}

// configure the maven publication
publishing {
    publications {
        create("mavenJava", MavenPublication::class) {
            artifactId = project.base.archivesName.get()
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}

