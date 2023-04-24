import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("fabric-loom")
    kotlin("jvm") version Version.KOTLIN
    kotlin("plugin.serialization") version Version.KOTLIN
    `maven-publish`
}

tasks.processResources {
    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "minecraft" to Version.MINECRAFT,
                "version" to project.version,
            ),
        )
    }
}

dependencies {
    minecraft(Lib.MINECRAFT)
    mappings(Lib.MAPPINGS)
    modImplementation(Lib.LOADER)
    modImplementation("net.fabricmc.fabric-api:fabric-api:${Version.FABRIC}")
    implementation(include(Lib.KOTLIN_STBLIB)!!)
    implementation(include(Lib.KOTLIN_STBLIB_JDK8)!!)
    implementation(include(Lib.COROUTINES_CORE)!!)
    implementation(include(Lib.COROUTINES_JDK8)!!)
    implementation(include(Lib.SERIALIZATION_CORE)!!)
    implementation(include(Lib.SERIALIZATION_JSON)!!)
    implementation(include(Lib.SERIALIZATION_CBOR)!!)
    implementation(include(Lib.DATETIME)!!)
    implementation(include(Lib.FUEL)!!)
    implementation(include(Lib.FUEL_CORUTINES)!!)
    runtimeOnly(include(Lib.RESULT)!!)
    implementation(include(Lib.MIXIN_EXTRAS)!!)
    api(include(project(":raincoat-protocol"))!!)
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

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlinx.serialization.ExperimentalSerializationApi"
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
