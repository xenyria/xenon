/*
 * Copyright (c) 2026 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

import com.github.jengelman.gradle.plugins.shadow.ShadowBasePlugin.Companion.shadow
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
    id("maven-publish")
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "9.1.0"
    kotlin("plugin.serialization") version "2.3.0"
}

val minecraftVersion = project.findProperty("minecraft_version")
val loaderVersion = project.findProperty("loader_version")
val fabricKotlinVersion = project.findProperty("fabric_kotlin_version")
val fabricApiVersion = project.findProperty("fabric_api_version")
val yaclVersion = project.findProperty("yacl_version")
val modmenuVersion = project.findProperty("modmenu_version")

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 25
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

repositories {
    // Add repositories to retrieve artifacts from in here.
    // You should only use this when depending on other mods because
    // Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html
    // for more information about repositories.
    // YACL
    maven("https://maven.isxander.dev/releases") {
        name = "Xander Maven"
    }
    // ModMenu
    maven("https://maven.terraformersmc.com/") {
        name = "Terraformers"
    }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    implementation("net.fabricmc:fabric-loader:${loaderVersion}")

    implementation("net.fabricmc.fabric-api:fabric-api:${fabricApiVersion}")
    implementation("net.fabricmc:fabric-language-kotlin:${fabricKotlinVersion}")
    implementation("dev.isxander:yet-another-config-lib:${yaclVersion}")
    implementation("com.terraformersmc:modmenu:${modmenuVersion}")

    shadow(libs.kotlin.serialization.json)
    shadow(libs.discord.game.sdk4j)
    shadow(project(":core"))
    shadow(project(":client"))
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
    options.release = 25
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

tasks.jar {
    dependsOn(tasks.shadowJar)
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

