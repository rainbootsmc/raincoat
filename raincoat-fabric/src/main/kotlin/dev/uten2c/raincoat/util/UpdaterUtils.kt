package dev.uten2c.raincoat.util

object UpdaterUtils {
    private const val MAVEN_REPO_URL = "https://rainboots-maven.uten2c.dev"
    private const val ARTIFACT_NAME = "raincoat-fabric"
    const val MAVEN_METAFILE_URL = "$MAVEN_REPO_URL/dev/uten2c/$ARTIFACT_NAME/maven-metadata.xml"

    fun getArtifactUrl(version: String): String {
        return "$MAVEN_REPO_URL/dev/uten2c/$ARTIFACT_NAME/$version/$ARTIFACT_NAME-$version.jar"
    }
}
