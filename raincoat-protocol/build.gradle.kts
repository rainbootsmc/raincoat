plugins {
    java
    `maven-publish`
    kotlin("jvm") version Version.KOTLIN
    kotlin("plugin.serialization") version Version.KOTLIN
}

dependencies {
    implementation("org.jetbrains:annotations:23.1.0")
    implementation(Lib.KOTLIN_STBLIB)
    implementation(Lib.KOTLIN_STBLIB_JDK8)
    implementation(Lib.SERIALIZATION_CORE)
    implementation(Lib.SERIALIZATION_JSON)
    implementation(Lib.SERIALIZATION_CBOR)
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
