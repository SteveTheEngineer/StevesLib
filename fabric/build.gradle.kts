import net.fabricmc.loom.LoomGradleExtension

plugins {
    id("com.github.johnrengelman.shadow")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    accessWidenerPath.set(
        project(":common").extensions.getByName<LoomGradleExtension>("loom").accessWidenerPath.get()
    )
}

val common = configurations.create("common")
val shadowCommon = configurations.create("shadowCommon")

configurations {
    compileClasspath.get().extendsFrom(common)
    runtimeClasspath.get().extendsFrom(common)
    getByName("developmentFabric").extendsFrom(common)
}

repositories {
    
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${rootProject.property("fabric_api_version")}")

    modImplementation("net.fabricmc:fabric-language-kotlin:${rootProject.property("fabric_language_kotlin_version")}")
    modApi("dev.architectury:architectury-fabric:${rootProject.property("architectury_version")}") {
        exclude(group = "net.fabricmc", module = "fabric-loader")
    }
    include(
        modApi("teamreborn:energy:3.0.0") {
            exclude(group = "net.fabricmc.fabric-api")
            exclude(group = "net.fabricmc", module = "fabric-loader")
            isTransitive = false
        }
    )

    common(project(":common", "namedElements")) { isTransitive = false }
    shadowCommon(project(":common", "transformProductionFabric")) { isTransitive = false }
}

val replaceTokens: Map<String, String> by rootProject.extra
tasks.processResources {
    inputs.properties(replaceTokens)

    filesMatching("fabric.mod.json") {
        expand(replaceTokens)
    }

    from(
        project(":common").extensions.getByName<LoomGradleExtension>("loom").accessWidenerPath.get()
    )
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
        create<MavenPublication>("mavenFabric") {
            artifactId = "${rootProject.property("archives_base_name")}-${project.name}"
            from(components.getByName("java"))
        }
    }

    repositories {

    }
}
