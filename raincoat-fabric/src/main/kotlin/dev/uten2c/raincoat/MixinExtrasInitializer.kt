package dev.uten2c.raincoat

import com.llamalad7.mixinextras.MixinExtrasBootstrap
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint

class MixinExtrasInitializer : PreLaunchEntrypoint {
    override fun onPreLaunch() {
        MixinExtrasBootstrap.init()
    }
}
