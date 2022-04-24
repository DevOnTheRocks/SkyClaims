import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    `java-library`
    idea
    id("com.github.ben-manes.versions") version "0.42.0"
    id("net.kyori.blossom") version "1.2.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("io.franzbecker.gradle-lombok") version "5.0.0"
    id("org.sonarqube") version "3.3"
    id("org.spongepowered.gradle.plugin") version "2.0.1"
}

val major: String by project
val minor: String by project
val patch: String by project
val api: String by project
val suffix: String by project

group = "net.mohron.skyclaims"
version = "${major}.${minor}.${patch}-${api}-${suffix}"
description = "SkyClaims is an Island Management plugin that integrates with GriefDefender."

repositories {
    mavenCentral()
    maven {
        name = "Sponge"
        url = uri("https://repo.spongepowered.org/maven")
    }
    maven {
        name = "Forge"
        url = uri("https://files.minecraftforge.net/maven")
    }
    maven {
        name = "GriefDefender"
        url = uri("https://repo.glaremasters.me/repository/bloodshot")
    }
    maven {
        name = "Nucleus"
        url = uri("https://repo.drnaylor.co.uk/artifactory/list/minecraft")
    }
    maven {
        name = "LuckPerms"
        url = uri("https://repo.lucko.me/")
    }
    maven {
        name = "Aikar"
        url = uri("https://repo.aikar.co/content/groups/aikar/")
    }
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
}

val spongeapi: String by project
val nucleus: String by project
val bstats: String by project
val griefdefender: String by project

dependencies {
    annotationProcessor("org.spongepowered:spongeapi:${spongeapi}")
    implementation("org.spongepowered:spongeapi:${spongeapi}")
    implementation("org.spongepowered:timings:1.0-SNAPSHOT")
    implementation(files("libs/griefdefender-sponge8-2.1.5.jar"))
//    implementation("com.griefdefender:api:2.1.0-SNAPSHOT")
    implementation("me.lucko.luckperms:luckperms-api:4.4")
    implementation("net.kyori:adventure-api:4.10.1")
    implementation("net.kyori:adventure-platform-spongeapi:4.0.1")
    implementation("net.kyori:adventure-text-serializer-spongeapi:4.0.0-SNAPSHOT")
    implementation("net.kyori:event-api:5.0.0-SNAPSHOT")
    implementation("co.aikar:acf-sponge:0.5.0-SNAPSHOT")
    implementation("io.github.nucleuspowered:nucleus-api:${nucleus}")
    implementation("org.bstats:bstats-sponge:${bstats}")
}

sponge {
    apiVersion(spongeapi)
    license("GPLv3")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("skyclaims") {
        displayName(project.name)
        entrypoint("net.mohron.skyclaims.SkyClaims")
        description(project.description)
        version(version.toString())
        links {
            homepage("https://devontherocks.github.io/SkyClaims")
            source("https://github.com/DevOnTheRocks/SkyClaims")
            issues("https://github.com/DevOnTheRocks/SkyClaims/issues")
        }
        contributor("Mohron") {
            description("Lead Developer")
        }
        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
    }
}

val javaTarget = 8
java {
    sourceCompatibility = JavaVersion.toVersion(javaTarget)
    targetCompatibility = JavaVersion.toVersion(javaTarget)
}

tasks.withType(JavaCompile::class).configureEach {
    options.apply {
        encoding = "utf-8" // Consistent source file encoding
        if (JavaVersion.current().isJava10Compatible) {
            release.set(javaTarget)
        }
    }
}

// Make sure all tasks which produce archives (jar, sources jar, javadoc jar, etc) produce more consistent output
tasks.withType(AbstractArchiveTask::class).configureEach {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}

lombok {
    version = "1.18.8"
    sha256 = ""
}

blossom {
    val pluginInfo = "src/main/java/net/mohron/skyclaims/PluginInfo.java"

    replaceToken("@NAME@", name, pluginInfo)
    replaceToken("@VERSION@", version, pluginInfo)
    replaceToken("@DESCRIPTION@", description, pluginInfo)
    replaceToken("@SPONGE_API@", spongeapi, pluginInfo)
    replaceToken("@GRIEF_DEFENDER@", griefdefender, pluginInfo)
    replaceToken("@NUCLEUS@", nucleus, pluginInfo)
}

tasks {
    shadowJar {
        archiveClassifier.set("plugin")
        dependencies {
            include(dependency("org.bstats:bstats-base:${bstats}"))
            include(dependency("org.bstats:bstats-sponge:${bstats}"))
        }
        relocate("org.bstats", "net.mohron.skyclaims.metrics")

        relocate("net.kyori", "com.griefdefender.lib.kyori")
        relocate("co.aikar.commands", "com.griefdefender.lib.aikar.commands")

        minimize()
    }

    build {
        dependsOn(shadowJar)
    }
}
