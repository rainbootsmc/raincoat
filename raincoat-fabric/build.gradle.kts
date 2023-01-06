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
    arrayOf(
        "fabric-api-base",
        "fabric-command-api-v2",
        "fabric-lifecycle-events-v1",
        "fabric-registry-sync-v0",
        "fabric-resource-loader-v0",
        "fabric-key-binding-api-v1",
        "fabric-networking-api-v1",
    ).forEach { modImplementation(fabricApi.module(it, Version.FABRIC)) }
    includeAndApi(project(":raincoat-protocol"))
    includeAndApi(Lib.MIXIN_EXTRAS)
    annotationProcessor(Lib.MIXIN_EXTRAS)
    modApi("com.terraformersmc:modmenu:4.1.2")
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
