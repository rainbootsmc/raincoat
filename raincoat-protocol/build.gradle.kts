plugins {
    java
    `maven-publish`
}

dependencies {
    implementation("org.jetbrains:annotations:23.1.0")
}

java {
    withJavadocJar()
    withSourcesJar()
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
