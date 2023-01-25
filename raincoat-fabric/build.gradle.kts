plugins {
    id("fabric-loom")
    `maven-publish`
}

tasks.processResources {
    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "minecraft" to Version.MINECRAFT,
                "version" to project.version,
            )
        )
    }
}

dependencies {
    minecraft(Lib.MINECRAFT)
    mappings(Lib.MAPPINGS)
    modImplementation(Lib.LOADER)
    modImplementation("net.fabricmc.fabric-api:fabric-api:${Version.FABRIC}")
    api(include(project(":raincoat-protocol"))!!)
    api(include(Lib.MIXIN_EXTRAS)!!)
    annotationProcessor(Lib.MIXIN_EXTRAS)
    modApi(Lib.MOD_MENU)
}

loom {
    accessWidenerPath.set(file("src/main/resources/raincoat.accesswidener"))

    runs {
        getByName("client") {
            isIdeConfigGenerated = true
            programArgs("--username", "Dev")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("main") {
            from(components["java"])
        }
    }
    val publishPath = publishPath
    if (publishPath != null) {
        repositories {
            maven {
                url = uri(publishPath)
            }
        }
    }
}
