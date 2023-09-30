import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("fabric-loom")
    id("io.github.juuxel.loom-vineflower")
    kotlin("jvm") version Version.KOTLIN
    kotlin("plugin.serialization") version Version.KOTLIN
    id("com.modrinth.minotaur") version "2.+"
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
    modImplementation(Lib.FABRIC_API)
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

modrinth {
    token.set(Modrinth.token)
    projectId.set(Modrinth.PROJECT_ID)
    versionNumber.set(Version.project)
    versionType.set("release")
    uploadFile.set(tasks.getByName("remapJar"))
    gameVersions.add(Version.MINECRAFT)
    dependencies {
        required.project("fabric-api")
        optional.project("modmenu")
    }

    syncBodyFrom.set(rootProject.file("README.md").readText())
}
