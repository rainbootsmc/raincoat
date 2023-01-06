package dev.uten2c.raincoat.util;

import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

public final class UpdaterUtils {
    public static final @NotNull String MAVEN_REPO_URL = "https://rainboots-maven.uten2c.dev";
    public static final @NotNull String ARTIFACT_NAME = "raincoat-fabric";
    public static final @NotNull String MAVEN_METAFILE_URL = MAVEN_REPO_URL + "/dev/uten2c/" + ARTIFACT_NAME + "/maven-metadata.xml";

    private UpdaterUtils() {
    }

    public static @NotNull URL getArtifactUrl(@NotNull String version) throws MalformedURLException {
        return new URL(UpdaterUtils.MAVEN_REPO_URL + "/dev/uten2c/" + ARTIFACT_NAME + "/" + version + "/" + ARTIFACT_NAME + "-" + version + ".jar");
    }
}
