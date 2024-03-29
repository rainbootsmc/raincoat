package dev.uten2c.raincoat.updater

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitByteArray
import com.mojang.logging.LogUtils
import dev.uten2c.raincoat.MOD_ID
import dev.uten2c.raincoat.util.UpdaterUtils
import kotlinx.coroutines.*
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.SemanticVersion
import java.nio.file.AccessDeniedException
import java.nio.file.Files
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.jvm.optionals.getOrNull

object Updater {
    private val logger = LogUtils.getLogger()
    private val modContainer = FabricLoader.getInstance().getModContainer(MOD_ID).get()
    private var updatableVersion: String? = null
    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    @JvmStatic
    val isUpdateAvailable: Boolean
        get() = updatableVersion != null

    suspend fun startDownload(): UpdateStatus {
        val updatableVersion = updatableVersion ?: return UpdateStatus.ErrorReturnToTitle("アップデート可能なバージョンが見つかりませんでした")
        val bytes = runCatching {
            Fuel.get(UpdaterUtils.getArtifactUrl(updatableVersion)).awaitByteArray()
        }.onFailure {
            logger.error("MODのダウンロードに失敗しました", it)
            return UpdateStatus.ErrorReturnToTitle("MODのダウンロードに失敗しました")
        }.getOrThrow()
        runCatching {
            val jarPath = modContainer.origin.paths[0]
            withContext(Dispatchers.IO) {
                Files.newOutputStream(jarPath).use { out ->
                    out.write(bytes)
                }
            }
        }.onFailure {
            if (it is AccessDeniedException) {
                logger.error("MODファイルへの書き込みに失敗しました", it)
                return UpdateStatus.ErrorReturnToTitle("MODファイルへの書き込みに失敗しました")
            }
            logger.error("予期しないエラーが発生しました", it)
            return UpdateStatus.ErrorShouldRestart("予期しないエラーが発生しました")
        }
        return UpdateStatus.Completed
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
                runCatching {
                    SemanticVersion.parse(modContainer.metadata.version.friendlyString)
                }.onSuccess { currentVersion ->
                    if (shouldUpdate(currentVersion, releaseVersion)) {
                        updatableVersion = releaseVersion.friendlyString
                    }
                }.onFailure {
                    logger.warn("バージョンのパースに失敗しました")
                }
            }
            .onFailure {
                logger.error("最新バージョンの確認に失敗しました", it)
            }
    }

    private suspend fun fetchReleaseVersion(): Result<SemanticVersion> {
        return kotlin.runCatching {
            val bytes = Fuel.get(UpdaterUtils.MAVEN_METAFILE_URL).awaitByteArray()
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val document = builder.parse(bytes.inputStream())
            val element = document.documentElement
            val release = element.getElementsByTagName("release")
            val versionString = release.item(0).textContent
            SemanticVersion.parse(versionString)
        }
    }

    private fun shouldUpdate(current: SemanticVersion, latest: SemanticVersion): Boolean {
        return equalsMinecraftVersion(current, latest) &&
                current.buildKey.getOrNull() != latest.buildKey.getOrNull()
    }

    private fun equalsMinecraftVersion(a: SemanticVersion, b: SemanticVersion): Boolean {
        return a.getVersionComponent(0) == b.getVersionComponent(0) &&
                a.getVersionComponent(1) == b.getVersionComponent(1) &&
                a.getVersionComponent(2) == b.getVersionComponent(2)
    }
}
