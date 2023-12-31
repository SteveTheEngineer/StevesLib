dependencies {
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")
    modApi("dev.architectury:architectury:${rootProject.property("architectury_version")}")
}

architectury {
    common {}
}

loom {
    accessWidenerPath.set(
        File("src/main/resources/steveslib.accesswidener")
    )
}

publishing {
    publications {
        create<MavenPublication>("mavenCommon") {
            artifactId = rootProject.property("archives_base_name") as String
            from(components.getByName("java"))
        }
    }

    repositories {

    }
}