pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net")
        gradlePluginPortal()
    }
}

rootProject.name = "raincoat"

include(
    "raincoat-fabric",
    "raincoat-protocol",
)
