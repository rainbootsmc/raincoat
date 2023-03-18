package dev.uten2c.raincoat.updater

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitByteArray
import com.mojang.logging.LogUtils
import dev.uten2c.raincoat.MOD_ID
import dev.uten2c.raincoat.util.UpdaterUtils
import kotlinx.coroutines.*
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Files
import javax.xml.parsers.DocumentBuilderFactory

object Updater {
    private val logger = LogUtils.getLogger()
    private val modContainer = FabricLoader.getInstance().getModContainer(MOD_ID).get()
    private var updatableVersion: String? = null
    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @JvmStatic
    val isUpdateAvailable: Boolean
        get() = updatableVersion != null

    suspend fun startDownload(): Boolean {
        val updatableVersion = updatableVersion ?: return false
        val bytes = kotlin.runCatching {
            Fuel.get(UpdaterUtils.getArtifactUrl(updatableVersion)).awaitByteArray()
        }.onFailure {
            logger.error("MODのダウンロードに失敗しました", it)
            return false
        }.getOrThrow()
        val jarPath = modContainer.origin.paths[0]
        withContext(Dispatchers.IO) {
            Files.newOutputStream(jarPath).use { out ->
                out.write(bytes)
            }
        }
        return true
    }

    @JvmStatic
    fun asyncCheckUpdate() {
        scope.launch {
            checkUpdate()
        }
    }

    private suspend fun checkUpdate() {
        fetchReleaseVersion()
            .onSuccess { releaseVersion ->
                val currentVersion = modContainer.metadata.version
                if (currentVersion.friendlyString != releaseVersion) {
                    updatableVersion = releaseVersion
                }
            }
            .onFailure {
                logger.error("最新バージョンの確認に失敗しました", it)
            }
    }

    private suspend fun fetchReleaseVersion(): Result<String> {
        return kotlin.runCatching {
            val bytes = Fuel.get(UpdaterUtils.MAVEN_METAFILE_URL).awaitByteArray()
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val document = builder.parse(bytes.inputStream())
            val element = document.documentElement
            val release = element.getElementsByTagName("release")
            release.item(0).textContent
        }
    }
}
