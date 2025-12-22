package yarden_zamir.column_quick_swap

import net.minecraft.resources.ResourceLocation
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import yarden_zamir.column_quick_swap.networking.Networking

@Mod(ColumnQuickSwap.ID)
object ColumnQuickSwap {
    const val ID = "column_quick_swap"

    val LOGGER: Logger = LogManager.getLogger(ID)

    fun id(path: String) = ResourceLocation(ID, path)

    init {
        LOGGER.info("Column Quick Swap initialized")
        Networking // Initialize networking on both sides
    }
}
