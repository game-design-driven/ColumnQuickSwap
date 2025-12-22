package yarden_zamir.column_quick_swap.networking

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraftforge.network.NetworkEvent
import yarden_zamir.column_quick_swap.ColumnQuickSwap
import java.util.function.Supplier

/**
 * Packet to swap an item from a column slot (inventory row above hotbar) with the hotbar slot.
 *
 * @param hotbarSlot The hotbar slot index (0-8)
 * @param columnRow The row in the column (0 = row closest to hotbar, 1 = middle, 2 = top row)
 */
data class C2SSwapColumnSlotPacket(val hotbarSlot: Int, val columnRow: Int) {
    companion object {
        val CODEC: Codec<C2SSwapColumnSlotPacket> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("hotbarSlot").forGetter { it.hotbarSlot },
                Codec.INT.fieldOf("columnRow").forGetter { it.columnRow }
            ).apply(instance, ::C2SSwapColumnSlotPacket)
        }

        fun handle(packet: C2SSwapColumnSlotPacket, context: Supplier<NetworkEvent.Context>) = runCatching {
            val player = context.get().sender
            if (player == null) {
                ColumnQuickSwap.LOGGER.warn("Received C2SSwapColumnSlotPacket from null player.")
                return@runCatching
            }

            val inventory = player.inventory

            // Validate hotbar slot
            if (packet.hotbarSlot !in 0..8) {
                ColumnQuickSwap.LOGGER.warn("Invalid hotbar slot ${packet.hotbarSlot} from ${player.displayName.string}")
                return@runCatching
            }

            // Validate column row (0 = closest to hotbar, 1 = middle, 2 = top)
            if (packet.columnRow !in 0..2) {
                ColumnQuickSwap.LOGGER.warn("Invalid column row ${packet.columnRow} from ${player.displayName.string}")
                return@runCatching
            }

            // Calculate the inventory slot index
            // Row 0 (closest to hotbar) = slots 27-35
            // Row 1 (middle) = slots 18-26
            // Row 2 (top) = slots 9-17
            val inventorySlot = when (packet.columnRow) {
                0 -> 27 + packet.hotbarSlot
                1 -> 18 + packet.hotbarSlot
                2 -> 9 + packet.hotbarSlot
                else -> return@runCatching
            }

            // Swap items
            val hotbarItem = inventory.getItem(packet.hotbarSlot)
            val columnItem = inventory.getItem(inventorySlot)

            inventory.setItem(packet.hotbarSlot, columnItem)
            inventory.setItem(inventorySlot, hotbarItem)

            // Mark inventory as changed
            inventory.setChanged()
        }.onFailure {
            ColumnQuickSwap.LOGGER.error("Error handling C2SSwapColumnSlotPacket", it)
        }
    }
}
