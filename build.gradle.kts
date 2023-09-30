plugins {
    id("fabric-loom") version "1.3-SNAPSHOT" apply false
    id("io.github.juuxel.loom-vineflower") version Version.LOOM_VINEFLOWER apply false
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
