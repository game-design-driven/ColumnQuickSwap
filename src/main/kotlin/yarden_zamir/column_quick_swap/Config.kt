package yarden_zamir.column_quick_swap

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.fml.loading.FMLPaths
import kotlin.io.path.createFile
import kotlin.io.path.div
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream
import kotlin.io.path.writeText

internal val json = Json {
    encodeDefaults = true
    prettyPrint = true
}

@OnlyIn(Dist.CLIENT)
@Serializable
data class ClientConfig(
    /** Ticks to hold key before popup opens (0 = instant) */
    val pressTicks: Int = 0,
    /** Play sound when swapping items */
    val playSound: Boolean = true,
    /** Sound volume (0.0 to 1.0) */
    val soundVolume: Float = 0.3f,
    /** Sound pitch (higher = higher pitch) */
    val soundPitch: Float = 1.4f,
    /** Close popup and select item when key is released */
    val closeOnRelease: Boolean = true,
    /** Highlight color for hovered slot (ARGB hex) */
    val highlightColor: Int = 0x80FFFFFF.toInt(),
    /** Show item tooltips on hover */
    val showTooltips: Boolean = true,
    /** Allow scrolling to change hotbar slot while popup is open */
    val allowScroll: Boolean = true
) {
    companion object {
        private val path = FMLPaths.CONFIGDIR.get() / "${ColumnQuickSwap.ID}.client.json"

        var config = ClientConfig()

        @OptIn(ExperimentalSerializationApi::class)
        fun reload() {
            runCatching {
                path.createFile()
                path.writeText("{}")
            }
            config = json.decodeFromStream(path.inputStream())
            json.encodeToStream(config, path.outputStream())
        }
    }
}
