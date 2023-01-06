package dev.uten2c.raincoat.updater;

import dev.uten2c.raincoat.RaincoatMod;
import dev.uten2c.raincoat.util.UpdaterUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class Updater implements PreLaunchEntrypoint {
    private static @Nullable String updatableVersion;

    @Override
    public void onPreLaunch() {
        asyncFetchReleaseVersion();
    }

    public static boolean isUpdateAvailable() {
        return Updater.updatableVersion != null;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void tryStartDownload(@NotNull Runnable completeCallback, @NotNull Runnable errorCallback) {
        var updatableVersion = Updater.updatableVersion;
        if (updatableVersion == null) {
            errorCallback.run();
            return;
        }
        try (var in = downloadMod(updatableVersion)) {
            var modContainer = FabricLoader.getInstance().getModContainer(RaincoatMod.MOD_ID).get();
            var jarPath = modContainer.getOrigin().getPaths().get(0);
            var out = new DataOutputStream(Files.newOutputStream(jarPath));
            out.write(in.readAllBytes());
            completeCallback.run();
        } catch (Exception e) {
            e.printStackTrace();
            errorCallback.run();
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static void asyncFetchReleaseVersion() {
        new Thread(() -> {
            try {
                var releaseVersion = fetchReleaseVersion();
                if (releaseVersion == null) {
                    return;
                }
                var modContainer = FabricLoader.getInstance().getModContainer(RaincoatMod.MOD_ID).get();
                var currentVersion = modContainer.getMetadata().getVersion();
                if (!currentVersion.getFriendlyString().equals(releaseVersion)) {
                    updatableVersion = releaseVersion;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private static @Nullable String fetchReleaseVersion() throws IOException {
        var url = new URL(UpdaterUtils.MAVEN_METAFILE_URL);
        try (var in = download(url)) {
            var factory = DocumentBuilderFactory.newInstance();
            var builder = factory.newDocumentBuilder();
            var document = builder.parse(in);
            var element = document.getDocumentElement();
            var release = element.getElementsByTagName("release");
            return release.item(0).getTextContent();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static InputStream downloadMod(@NotNull String version) throws Exception {
        var url = UpdaterUtils.getArtifactUrl(version);
        return download(url);
    }

    private static InputStream download(@NotNull URL url) throws Exception {
        var conn = (HttpURLConnection) url.openConnection();
        conn.setAllowUserInteraction(false);
        conn.setInstanceFollowRedirects(true);
        conn.setRequestMethod("GET");
        conn.connect();
        var responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("Http status code: " + responseCode);
        }
        return conn.getInputStream();
    }
}
