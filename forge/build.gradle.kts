import net.fabricmc.loom.LoomGradleExtension

plugins {
    id("com.github.johnrengelman.shadow")
}

architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    val widenerFile = project(":common").extensions.getByName<LoomGradleExtension>("loom").accessWidenerPath.get()
    accessWidenerPath.set(widenerFile)

    forge {
        mixinConfigs("steveslib.mixins.json", "steveslib-common.mixins.json")
        convertAccessWideners.set(true)
    }
}

val common = configurations.create("common")
val shadowCommon = configurations.create("shadowCommon")

configurations {
    compileClasspath.get().extendsFrom(common)
    runtimeClasspath.get().extendsFrom(common)
    getByName("developmentForge").extendsFrom(common)
}

repositories {
    maven("https://thedarkcolour.github.io/KotlinForForge")
}

dependencies {
    forge("net.minecraftforge:forge:${project.property("forge_version")}")

    implementation("thedarkcolour:kotlinforforge:${project.property("kotlin_for_forge_version")}")
    modApi("dev.architectury:architectury-forge:${rootProject.property("architectury_version")}")

    common(project(":common", "namedElements")) { isTransitive = false }
    shadowCommon(project(":common", "transformProductionForge")) { isTransitive = false }
}

val replaceTokens: Map<String, String> by rootProject.extra
tasks.processResources {
    inputs.properties(replaceTokens)

    filesMatching("META-INF/mods.toml") {
        expand(replaceTokens)
    }
}

tasks.shadowJar {
    configurations = listOf(shadowCommon)
    archiveClassifier.set("dev-shadow")
}

tasks.remapJar {
    inputFile.set(tasks.shadowJar.get().archiveFile)
    dependsOn(tasks.shadowJar.get())
    archiveClassifier.set(null as String?)
}

tasks.jar {
    archiveClassifier.set("dev")
}

tasks.sourcesJar {
    val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
    dependsOn(commonSources)
    from(commonSources.archiveFile.map(::zipTree))
}

components.named<AdhocComponentWithVariants>("java") {
    withVariantsFromConfiguration(project.configurations.shadowRuntimeElements.get()) {
        skip()
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenForge") {
            artifactId = "${rootProject.property("archives_base_name")}-${project.name}"
            from(components.getByName("java"))
        }
    }

    repositories {

    }
}