package dev.uten2c.raincoat.updater

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint

class UpdaterEntrypoint : PreLaunchEntrypoint {
    override fun onPreLaunch() {
        Updater.asyncCheckUpdate()
    }
}
