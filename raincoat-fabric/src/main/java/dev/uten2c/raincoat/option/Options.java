package dev.uten2c.raincoat.option;

import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Options {
    private static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("raincoat.json");
    private static final Gson gson = new Gson();
    private static double adsRelativeSensibility = 1f;
    private static double scope2xRelativeSensibility = 0.75f;
    private static double scope4xRelativeSensibility = 0.5f;
    private static boolean adsHold = false;

    private Options() {
    }

    public static void load() {
        if (Files.exists(configPath)) {
            try (var in = Files.newInputStream(configPath)) {
                var string = new String(in.readAllBytes());
                var raincoatOptions = gson.fromJson(string, RaincoatOptions.class);
                adsRelativeSensibility = raincoatOptions.getScope4xRelativeSensibility();
                scope2xRelativeSensibility = raincoatOptions.getScope2xRelativeSensibility();
                scope4xRelativeSensibility = raincoatOptions.getScope4xRelativeSensibility();
                adsHold = raincoatOptions.isAdsHold();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void save() {
        if (Files.notExists(configPath)) {
            try {
                Files.createFile(configPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        var options = new RaincoatOptions(adsRelativeSensibility, scope2xRelativeSensibility, scope4xRelativeSensibility, adsHold);
        var json = gson.toJson(options);
        try {
            Files.writeString(configPath, json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static double getAdsRelativeSensibility() {
        return adsRelativeSensibility;
    }

    public static void setAdsRelativeSensibility(double adsRelativeSensibility) {
        Options.adsRelativeSensibility = adsRelativeSensibility;
    }

    public static double getScope2xRelativeSensibility() {
        return scope2xRelativeSensibility;
    }

    public static void setScope2xRelativeSensibility(double scope2xRelativeSensibility) {
        Options.scope2xRelativeSensibility = scope2xRelativeSensibility;
    }

    public static double getScope4xRelativeSensibility() {
        return scope4xRelativeSensibility;
    }

    public static void setScope4xRelativeSensibility(double scope4xRelativeSensibility) {
        Options.scope4xRelativeSensibility = scope4xRelativeSensibility;
    }

    public static boolean isAdsHold() {
        return adsHold;
    }

    public static void setAdsHold(boolean adsHold) {
        Options.adsHold = adsHold;
    }
}
