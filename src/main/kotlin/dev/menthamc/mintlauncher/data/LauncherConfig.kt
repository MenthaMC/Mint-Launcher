package dev.menthamc.mintlauncher.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Serializable
data class LauncherConfig(
    val installDir: String = "",
    val javaPath: String = "java",
    val maxMemory: String = "",
    val extraJvmArgs: String = "",
    val serverArgs: String = "",
    val jarName: String = "",
    val jarHash: String = "",
    val lastSelectedReleaseTag: String? = null
)

class LauncherConfigStore(
    private val configPath: Path = Paths.get("mint-launcher.json")
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun load(): LauncherConfig {
        val loaded = try {
            if (Files.exists(configPath)) {
                val text = Files.readString(configPath)
                json.decodeFromString(LauncherConfig.serializer(), text)
            } else {
                LauncherConfig()
            }
        } catch (_: Exception) {
            LauncherConfig()
        }
        val defaultDir = Paths.get("").toAbsolutePath().toString()
        return if (loaded.installDir.isBlank()) loaded.copy(installDir = defaultDir) else loaded
    }

    fun save(config: LauncherConfig) {
        val text = json.encodeToString(LauncherConfig.serializer(), config)
        Files.writeString(configPath, text)
    }
}
