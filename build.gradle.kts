plugins {
    id("fabric-loom") version Version.LOOM apply false
}

subprojects {
    group = "dev.uten2c"
    version = Version.project

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.terraformersmc.com/releases/")
    }
}

tasks.create("publishAll") {
    group = "publishing"
    dependsOn(
        tasks.getByPath(":raincoat-fabric:publish"),
        tasks.getByPath(":raincoat-protocol:publish"),
    )
}
