package dev.uten2c.raincoat.option;

public class RaincoatOptions {
    private double adsRelativeSensibility;
    private double scope2xRelativeSensibility;
    private double scope4xRelativeSensibility;
    private boolean adsHold;

    public RaincoatOptions(double adsRelativeSensibility, double scope2xRelativeSensibility, double scope4xRelativeSensibility, boolean adsHold) {
        this.adsRelativeSensibility = adsRelativeSensibility;
        this.scope2xRelativeSensibility = scope2xRelativeSensibility;
        this.scope4xRelativeSensibility = scope4xRelativeSensibility;
        this.adsHold = adsHold;
    }

    public double getAdsRelativeSensibility() {
        return adsRelativeSensibility;
    }

    public void setAdsRelativeSensibility(double adsRelativeSensibility) {
        this.adsRelativeSensibility = adsRelativeSensibility;
    }

    public double getScope2xRelativeSensibility() {
        return scope2xRelativeSensibility;
    }

    public void setScope2xRelativeSensibility(double scope2xRelativeSensibility) {
        this.scope2xRelativeSensibility = scope2xRelativeSensibility;
    }

    public double getScope4xRelativeSensibility() {
        return scope4xRelativeSensibility;
    }

    public void setScope4xRelativeSensibility(double scope4xRelativeSensibility) {
        this.scope4xRelativeSensibility = scope4xRelativeSensibility;
    }

    public boolean isAdsHold() {
        return adsHold;
    }

    public void setAdsHold(boolean adsHold) {
        this.adsHold = adsHold;
    }
}
