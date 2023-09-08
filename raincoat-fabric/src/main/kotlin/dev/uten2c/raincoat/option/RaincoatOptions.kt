package dev.uten2c.raincoat.option

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RaincoatOptions(
    val adsRelativeSensibility: Double = 1.0,
    val scope2xRelativeSensibility: Double = 0.75,
    val scope4xRelativeSensibility: Double = 0.5,
    @SerialName("adsHold") val isAdsHold: Boolean = false,
    @SerialName("invertAttackKey") val isInvertAttackKey: Boolean = false,
    @SerialName("hideCrosshairWhenAds") val isHideCrosshairWhenAds: Boolean = true,
    @SerialName("narratorDisabled") val isNarratorDisabled: Boolean = true,
)
