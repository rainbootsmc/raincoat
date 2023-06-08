package dev.uten2c.raincoat.option

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import kotlin.io.path.exists
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.writeText

object OptionManager {
    private val configPath = FabricLoader.getInstance().configDir.resolve("raincoat.json")
    private val json = Json {
        encodeDefaults = true
    }
    @JvmStatic
    var options: RaincoatOptions = RaincoatOptions()
        private set

    fun load() {
        if (configPath.notExists()) {
            return
        }
        runCatching {
            options = json.decodeFromString(configPath.readText())
        }.onFailure { it.printStackTrace() }
    }

    fun save(options: RaincoatOptions) {
        this.options = options
        configPath.writeText(json.encodeToString(options))
    }
}
