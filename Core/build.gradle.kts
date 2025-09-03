/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */

plugins {
    kotlin("jvm")
}

group = "net.xenyria.xenon"
version = "1.0-SNAPSHOT"

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.json:json:20250517")
    implementation("net.openhft:zero-allocation-hashing:0.16")
    implementation("com.github.JnCrMx:discord-game-sdk4j:v1.0.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}