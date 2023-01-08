package dev.uten2c.raincoat.option;

// recordにするとGsonでシリアライズできなくなる
@SuppressWarnings("ClassCanBeRecord")
public class RaincoatOptions {
    private final double adsRelativeSensibility;
    private final double scope2xRelativeSensibility;
    private final double scope4xRelativeSensibility;
    private final boolean adsHold;
    private final boolean invertAttackKey;

    public RaincoatOptions(double adsRelativeSensibility, double scope2xRelativeSensibility, double scope4xRelativeSensibility, boolean adsHold, boolean invertAttackKey) {
        this.adsRelativeSensibility = adsRelativeSensibility;
        this.scope2xRelativeSensibility = scope2xRelativeSensibility;
        this.scope4xRelativeSensibility = scope4xRelativeSensibility;
        this.adsHold = adsHold;
        this.invertAttackKey = invertAttackKey;
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
}
