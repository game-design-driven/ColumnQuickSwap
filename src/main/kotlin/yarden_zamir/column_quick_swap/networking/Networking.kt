package yarden_zamir.column_quick_swap.networking

import net.minecraft.nbt.NbtOps
import net.minecraftforge.network.NetworkRegistry
import yarden_zamir.column_quick_swap.ColumnQuickSwap

@Suppress("DEPRECATION")
object Networking {
    private const val VERSION = "1"
    val ID = ColumnQuickSwap.id("main")
    val channel = NetworkRegistry.newSimpleChannel(ID, { VERSION }, VERSION::equals, VERSION::equals)

    init {
        channel.registerMessage(
            0,
            C2SSwapColumnSlotPacket::class.java,
            { message, buf -> buf.writeWithCodec(NbtOps.INSTANCE, C2SSwapColumnSlotPacket.CODEC, message) },
            { it.readWithCodec(NbtOps.INSTANCE, C2SSwapColumnSlotPacket.CODEC) },
            { packet, context -> C2SSwapColumnSlotPacket.handle(packet, context) }
        )
    }
}
