package dev.uten2c.raincoat.option;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

// recordにするとGsonでシリアライズできなくなる
public class RaincoatOptions {
    private static final RaincoatOptions DEFAULT = new RaincoatOptions(1f, 0.75f, 0.5f, false, false, true);
    private final double adsRelativeSensibility;
    private final double scope2xRelativeSensibility;
    private final double scope4xRelativeSensibility;
    private final boolean adsHold;
    private final boolean invertAttackKey;
    private final boolean hideCrosshairWhenAds;

    public RaincoatOptions(double adsRelativeSensibility, double scope2xRelativeSensibility, double scope4xRelativeSensibility, boolean adsHold, boolean invertAttackKey, @Nullable Boolean hideCrosshairWhenAds) {
        this.adsRelativeSensibility = adsRelativeSensibility;
        this.scope2xRelativeSensibility = scope2xRelativeSensibility;
        this.scope4xRelativeSensibility = scope4xRelativeSensibility;
        this.adsHold = adsHold;
        this.invertAttackKey = invertAttackKey;
        this.hideCrosshairWhenAds = hideCrosshairWhenAds == null || hideCrosshairWhenAds;
    }

    public double getAdsRelativeSensibility() {
        return adsRelativeSensibility;
    }

    public double getScope2xRelativeSensibility() {
        return scope2xRelativeSensibility;
    }

    public double getScope4xRelativeSensibility() {
        return scope4xRelativeSensibility;
    }

    public boolean isAdsHold() {
        return adsHold;
    }

    public boolean isInvertAttackKey() {
        return invertAttackKey;
    }

    public boolean isHideCrosshairWhenAds() {
        return hideCrosshairWhenAds;
    }

    public static class Deserializer implements JsonDeserializer<RaincoatOptions> {
        @Override
        public RaincoatOptions deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            final var jsonObject = json.getAsJsonObject();
            final var adsRelativeSensibility = jsonObject.get("adsRelativeSensibility");
            final var scope2xRelativeSensibility = jsonObject.get("scope2xRelativeSensibility");
            final var scope4xRelativeSensibility = jsonObject.get("scope4xRelativeSensibility");
            final var adsHold = jsonObject.get("adsHold");
            final var invertAttackKey = jsonObject.get("invertAttackKey");
            final var hideCrosshairWhenAds = jsonObject.get("hideCrosshairWhenAds");
            return new RaincoatOptions(
                    adsRelativeSensibility == null ? DEFAULT.adsRelativeSensibility : adsRelativeSensibility.getAsDouble(),
                    scope2xRelativeSensibility == null ? DEFAULT.scope2xRelativeSensibility : scope2xRelativeSensibility.getAsDouble(),
                    scope4xRelativeSensibility == null ? DEFAULT.scope4xRelativeSensibility : scope4xRelativeSensibility.getAsDouble(),
                    adsHold == null ? DEFAULT.adsHold : adsHold.getAsBoolean(),
                    invertAttackKey == null ? DEFAULT.invertAttackKey : invertAttackKey.getAsBoolean(),
                    hideCrosshairWhenAds == null ? DEFAULT.hideCrosshairWhenAds : hideCrosshairWhenAds.getAsBoolean()
            );
        }
    }
}
