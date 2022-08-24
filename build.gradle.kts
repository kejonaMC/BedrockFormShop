/*
 * This file was generated by the Gradle 'init' task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    java
    `maven-publish`
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    maven {
        url = uri("https://repo.opencollab.dev/maven-snapshots/")
    }

    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    implementation("org.jetbrains:annotations:23.0.0")
    implementation("org.projectlombok:lombok:1.18.24")
    implementation("com.github.Gypopo:EconomyShopGUI-API:1.2.0")
    compileOnly("org.spigotmc:spigot-api:1.15-R0.1-SNAPSHOT")
    compileOnly("org.geysermc.floodgate:api:2.2.0-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
}

group = "dev.kejona"
version = "1.0-SNAPSHOT"
description = "BedrockFormShop"
java.sourceCompatibility = JavaVersion.VERSION_14

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.jar {
    archiveFileName.set("BedrockFormShop.jar")
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
