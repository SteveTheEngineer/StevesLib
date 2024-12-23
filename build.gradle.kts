import net.fabricmc.loom.LoomGradleExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("architectury-plugin") version "3.4.151"
    id("dev.architectury.loom") version "1.4.373" apply false

    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    kotlin("jvm") apply false
}

val minecraftVersion = rootProject.property("minecraft_version") as String
architectury {
    minecraft = minecraftVersion
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "kotlin")

    extensions.configure<LoomGradleExtension>("loom") {
        silentMojangMappingsLicense()
    }

    val loom = extensions.getByName<LoomGradleExtension>("loom")

    repositories {
        maven("https://maven.parchmentmc.org")
        maven("https://maven.shedaniel.me")
        maven("https://modmaven.dev")
        maven("https://maven.terraformersmc.com/releases")
    }

    dependencies {
        add("compileOnly", kotlin("stdlib"))

        add("minecraft", "com.mojang:minecraft:$minecraftVersion")

        add("mappings", loom.layered {
            officialMojangMappings()
            parchment("org.parchmentmc.data:parchment-${rootProject.property("parchment_version")}@zip")
        })
    }

    tasks.named<KotlinCompile>("compileKotlin") {
        kotlinOptions.jvmTarget = "17"
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")

    extensions.configure<BasePluginExtension>("base") {
        archivesName.set(rootProject.property("archives_base_name") as String)
    }
    version = rootProject.property("mod_version") as String
    group = rootProject.property("maven_group") as String

    tasks.named<JavaCompile>("compileJava") {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    extensions.configure<JavaPluginExtension>("java") {
        withSourcesJar()
    }
}

val replaceTokens by extra {
    mapOf(
        "version" to project.version,
        "mcVersion" to rootProject.property("minecraft_version") as String,
        "forgeVersion" to rootProject.property("forge_version") as String,
        "kffVersion" to rootProject.property("kotlin_for_forge_version") as String,
        "license" to rootProject.property("mod_license") as String,
        "issues" to rootProject.property("mod_issues") as String,
        "name" to rootProject.property("mod_name") as String,
        "authors" to rootProject.property("mod_authors") as String,
        "description" to rootProject.property("mod_description") as String
    )
}
