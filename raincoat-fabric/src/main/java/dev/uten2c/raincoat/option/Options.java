package dev.uten2c.raincoat.option;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Options {
    private static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("raincoat.json");
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(RaincoatOptions.class, new RaincoatOptions.Deserializer())
            .create();
    private static double adsRelativeSensibility;
    private static double scope2xRelativeSensibility;
    private static double scope4xRelativeSensibility;
    private static boolean adsHold;
    private static boolean invertAttackKey;
    private static boolean hideCrosshairWhenAds;

    private Options() {
    }

    public static void load() {
        if (Files.exists(configPath)) {
            try (var in = Files.newInputStream(configPath)) {
                var string = new String(in.readAllBytes());
                var raincoatOptions = gson.fromJson(string, RaincoatOptions.class);
                adsRelativeSensibility = raincoatOptions.getAdsRelativeSensibility();
                scope2xRelativeSensibility = raincoatOptions.getScope2xRelativeSensibility();
                scope4xRelativeSensibility = raincoatOptions.getScope4xRelativeSensibility();
                adsHold = raincoatOptions.isAdsHold();
                invertAttackKey = raincoatOptions.isInvertAttackKey();
                hideCrosshairWhenAds = raincoatOptions.isHideCrosshairWhenAds();
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
        var options = new RaincoatOptions(
                adsRelativeSensibility,
                scope2xRelativeSensibility,
                scope4xRelativeSensibility,
                adsHold,
                invertAttackKey,
                hideCrosshairWhenAds
        );
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

    public static boolean isInvertAttackKey() {
        return invertAttackKey;
    }

    public static void setInvertAttackKey(boolean invertAttackKey) {
        Options.invertAttackKey = invertAttackKey;
    }

    public static boolean isHideCrosshairWhenAds() {
        return hideCrosshairWhenAds;
    }

    public static void setHideCrosshairWhenAds(boolean hideCrosshairWhenAds) {
        Options.hideCrosshairWhenAds = hideCrosshairWhenAds;
    }
}
